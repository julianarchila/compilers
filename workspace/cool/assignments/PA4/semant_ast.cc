#include "cool-tree.h"
#include "semant.h"
#include "utilities.h"

///////////////////////////////////////////////////////////////////
// Add to attrib / method table
///////////////////////////////////////////////////////////////////

void method_class::AddMethodToTable(Symbol class_name) {
    SEMLOG << "    Adding method " << name << std::endl;
    methodtables[class_name].addid(name, new method_class(copy_Symbol(name), formals->copy_list(), copy_Symbol(return_type), expr->copy_Expression()));
}

void method_class::AddAttribToTable(Symbol class_name) { }

void attr_class::AddMethodToTable(Symbol class_name) { }

void attr_class::AddAttribToTable(Symbol class_name) {
    SEMLOG << "Adding attrib " << name << std::endl;

    if (name == self) {
        classtable->semant_error(curr_class) << "Error! 'self' cannot be the name of an attribute in class " << curr_class->GetName() << std::endl;
    }
    if (attribtable.lookup(name) != NULL) {
        classtable->semant_error(curr_class) << "Error! attribute '" << name << "' already exists!" << std::endl;
        return;
    }

    attribtable.addid(name, new Symbol(type_decl));
}

///////////////////////////////////////////////////////////////////
// Type checking functions
///////////////////////////////////////////////////////////////////
void method_class::CheckFeatureType() {
    SEMLOG << "    Checking method \"" << name << "\"" << std::endl;

    // miramos que el tipo de retorno exista
    if (classtable->m_classes.find(return_type) == classtable->m_classes.end() && return_type != SELF_TYPE) {
        classtable->semant_error(curr_class) << "Error! return type " << return_type << " doesn't exist." << std::endl;
    }

    // Añadir los formals a un nuevo scope y hacer validaciones:
    // 1. Que no tengan nombres repetidos
    // 2. Que el tipo de cada formal exista
    // 3. Que el tipo no sea 'self'
    attribtable.enterscope();
    std::set<Symbol> used_names;
    for (int i = formals->first(); formals->more(i); i = formals->next(i)) {
        Symbol name = formals->nth(i)->GetName();
        if (used_names.find(name) != used_names.end()) {
            classtable->semant_error(curr_class) << "Error! formal name duplicated. " << std::endl;
        } else {
            used_names.insert(name);
        }

        Symbol type = formals->nth(i)->GetType();
        if (classtable->m_classes.find(type) == classtable->m_classes.end()) {
            classtable->semant_error(curr_class) << "Error! Cannot find class " << type << std::endl;
        }
        if (formals->nth(i)->GetName() == self) {
            classtable->semant_error(curr_class) << "Error! self in formal " << std::endl;
        }
        attribtable.addid(formals->nth(i)->GetName(), new Symbol(formals->nth(i)->GetType()));
    }
    
    // Verificar que el tipo de retorno sea correcto
    Symbol expr_type = expr->CheckExprType();
    if (classtable->CheckInheritance(return_type, expr_type) == false) {
        classtable->semant_error(curr_class) << "Error! return type is not ancestor of expr type. " << std::endl;
    }
    attribtable.exitscope();
}

void attr_class::CheckFeatureType() {
    SEMLOG << "    Checking atribute \"" << name << "\"" << std::endl;

    if (init->CheckExprType() == No_type) {
        SEMLOG << "NO INIT!" << std::endl;
    }
}

Symbol assign_class::CheckExprType() {
    Symbol* lvalue_type = attribtable.lookup(name);
    Symbol rvalue_type = expr->CheckExprType();
    if (lvalue_type == NULL) {
        classtable->semant_error(curr_class) << "Error! Cannot find lvalue " << name << std::endl;
        type = Object;
        return type;
    }
    if (classtable->CheckInheritance(*lvalue_type, rvalue_type) == false) {
        classtable->semant_error(curr_class) << "Error! lvalue is not an ancestor of rvalue. " << std::endl;
        type = Object;
        return type;
    }
    type = rvalue_type;
    return type;
}

Symbol static_dispatch_class::CheckExprType() {
    bool error = false;

    Symbol expr_class = expr->CheckExprType();

    if (classtable->CheckInheritance(type_name, expr_class) == false) {
        error = true;
        classtable->semant_error(curr_class) << "Error! Static dispatch class is not an ancestor." << std::endl;
    }

    SEMLOG << "Static dispatch: class = " << type_name << std::endl;

    // Find the method along the inheritance path.
    // We want the definition in a subclass.
    std::list<Symbol> path = classtable->GetInheritancePath(type_name);
    method_class* method = NULL;
    for (std::list<Symbol>::iterator iter = path.begin(); iter != path.end(); ++iter) {
        SEMLOG << "Looking for method in class " << *iter << std::endl;
        if ((method = methodtables[*iter].lookup(name)) != NULL) {
            break;
        }
    }

    if (method == NULL) {
        error = true;
        classtable->semant_error(curr_class) << "Error! Cannot find method '" << name << "'" << std::endl;
    }

    // Check the params.
    for (int i = actual->first(); actual->more(i); i = actual->next(i)) {
        Symbol actual_type = actual->nth(i)->CheckExprType();
        if (method != NULL) {
            Symbol formal_type = method->GetFormals()->nth(i)->GetType();
            if (classtable->CheckInheritance(formal_type, actual_type) == false) {
                classtable->semant_error(curr_class) << "Error! Actual type " << actual_type << " doesn't suit formal type " << formal_type << std::endl;
                error = true;
            }
        }
    }

    if (error) {
        type = Object;
    } else {
        type = method->GetType();
        if (type == SELF_TYPE) {
            type = type_name;
        }
    }

    return type;
}


bool dispatch_class::CheckParams(method_class* method){
    bool error = false;
    for (int i = actual->first(); actual->more(i); i = actual->next(i)) {
        Symbol actual_type = actual->nth(i)->CheckExprType();
        Symbol formal_type = method->GetFormals()->nth(i)->GetType();
        if (classtable->CheckInheritance(formal_type, actual_type) == false) {
            classtable->semant_error(curr_class) << "Error! Actual type " << actual_type << " doesn't suit formal type " << formal_type << std::endl;
            error = true;
        }
    }

    return error;
}

// dispatch: (expr).ID( [ expr [[, expr]]*] )
Symbol dispatch_class::CheckExprType() {
    bool error = false;

    Symbol expr_type = expr->CheckExprType();

    // Buscamos que name (ID) este definido en expr_type o en algun ancestro de expr_type
    std::list<Symbol> path = classtable->GetInheritancePath(expr_type);
    method_class* method = NULL;
    for (std::list<Symbol>::iterator iter = path.begin(); iter != path.end(); ++iter) {
        SEMLOG << "Looking for method in class " << *iter << std::endl;
        if ((method = methodtables[*iter].lookup(name)) != NULL) {
            break;
        }
    }

    if (method == NULL) {
        error = true;
        classtable->semant_error(curr_class) << "Error! Cannot find method '" << name << "'" << std::endl;
    }

    // Check the params.
    if (method != NULL) {
      bool checkParamsError = CheckParams(method);
      if (checkParamsError == true) error = true;
    }

    if (error) {
        type = Object;
    } else {
        type = method->GetType();
        if (type == SELF_TYPE) {
            type = expr_type;
        }
    }

    return type;
}

// IF - condition
Symbol cond_class::CheckExprType() {
    if (pred->CheckExprType() != Bool) {
        classtable->semant_error(curr_class) << "Error! Type of pred is not Bool." << std::endl;
    }

    Symbol then_type = then_exp->CheckExprType();
    Symbol else_type = else_exp->CheckExprType();

    if (else_type == No_type) {
        // Si no hay else:
        type = then_type;
    } else {
        type = classtable->LUB(then_type, else_type);
    }
    return type;
}

// while
Symbol loop_class::CheckExprType() {
    if (pred->CheckExprType() != Bool) {
        classtable->semant_error(curr_class) << "Error! Type of pred is not Bool." << std::endl;
    }
    body->CheckExprType();
    type = Object;
    return type;
}


// case ... of ...
// ===============
// Expression expr;
// Cases cases;
// 
Symbol typcase_class::CheckExprType() {

    expr->CheckExprType();

    Case branch;
    std::vector<Symbol> branch_types;
    std::vector<Symbol> branch_type_decls;

    for (int i = cases->first(); cases->more(i); i = cases->next(i)) {
        branch = cases->nth(i);
        Symbol branch_type = branch->CheckBranchType();
        branch_types.push_back(branch_type);
        branch_type_decls.push_back(((branch_class *)branch)->GetTypeDecl());
    }

    // Verificar que no haya casos respetidos
    for (int i = 0; i < branch_type_decls.size() - 1; ++i) {
        for (int j = i + 1; j < branch_type_decls.size(); ++j) {
            if (branch_type_decls[i] == branch_type_decls[j]) {
                classtable->semant_error(curr_class) << "Error! Two branches have same type." << std::endl;
            }
        }
    }

    // Calculamos el type de case mirando el lub de todos los branch_types
    type = branch_types[0];
    for (int i = 1; i < branch_types.size(); ++i) {
        type = classtable->LUB(type, branch_types[i]);
    }
    return type;
}

// branch
Symbol branch_class::CheckBranchType() {
    attribtable.enterscope();

    attribtable.addid(name, new Symbol(type_decl));
    Symbol type = expr->CheckExprType();

    attribtable.exitscope();

    return type;
}

// { [expr;]+ }
Symbol block_class::CheckExprType() {
    for (int i = body->first(); body->more(i); i = body->next(i)) {
        type = body->nth(i)->CheckExprType();
    }
    return type;
}

// let
Symbol let_class::CheckExprType() {
    if (identifier == self) {
        classtable->semant_error(curr_class) << "Error! self in let binding." << std::endl;
    }

    // add a new id into the environment
    attribtable.enterscope();
    attribtable.addid(identifier, new Symbol(type_decl));

    Symbol init_type = init->CheckExprType();
    // if there is an initialization expression
    if (init_type != No_type) {
        if (classtable->CheckInheritance(type_decl, init_type) == false) {
            classtable->semant_error(curr_class) << "Error! init value is not child." << std::endl;
        }
    }

    type = body->CheckExprType();
    attribtable.exitscope();
    return type;
}

Symbol plus_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '+' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Int;
    }
    return type;
}

// subtract - minus
Symbol sub_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '-' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Int;
    }
    return type;
}

Symbol mul_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '*' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Int;
    }
    return type;
}

Symbol divide_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '/' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Int;
    }
    return type;
}

// neg: ~
Symbol neg_class::CheckExprType() {
    if (e1->CheckExprType() != Int) {
        classtable->semant_error(curr_class) << "Error! '~' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Int;
    }
    return type;
}

// less than
Symbol lt_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '<' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Bool;
    }
    return type;
}

// equal
Symbol eq_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type == Int || e2_type == Int || e1_type == Bool || e2_type == Bool || e1_type == Str || e2_type == Str) {
        if (e1_type != e2_type) {
            classtable->semant_error(curr_class) << "Error! '=' meets different types." << std::endl;
            type = Object;
        } else {
            type = Bool;
        }
    } else {
        type = Bool;
    }
    return type;
}

// less equal
Symbol leq_class::CheckExprType() {
    Symbol e1_type = e1->CheckExprType();
    Symbol e2_type = e2->CheckExprType();
    if (e1_type != Int || e2_type != Int) {
        classtable->semant_error(curr_class) << "Error! '<=' meets non-Int value." << std::endl;
        type = Object;
    } else {
        type = Bool;
    }
    return type;
}

Symbol comp_class::CheckExprType() {
    if (e1->CheckExprType() != Bool) {
        classtable->semant_error(curr_class) << "Error! 'not' meets non-Bool value." << std::endl;
        type = Object;
    } else {
        type = Bool;
    }
    return type;
}

Symbol int_const_class::CheckExprType() {
    type = Int;
    return type;
}

Symbol bool_const_class::CheckExprType() {
    type = Bool;
    return type;
}

Symbol string_const_class::CheckExprType() {
    type = Str;
    return type;
}

// new TYPE
Symbol new__class::CheckExprType() {
    if (type_name != SELF_TYPE && classtable->m_classes.find(type_name) == classtable->m_classes.end()) {
        classtable->semant_error(curr_class) << "Error! type " << type_name << " doesn't exist." << std::endl;
    }
    type = type_name;
    return type;
}

Symbol isvoid_class::CheckExprType() {
    e1->CheckExprType();
    type = Bool;
    return type;
}

Symbol no_expr_class::CheckExprType() {
    return No_type;
}


Symbol object_class::CheckExprType() {
    if (name == self) {
        type = SELF_TYPE;
        return type;
    }

    Symbol* found_type = attribtable.lookup(name);
    if (found_type == NULL) {
        classtable->semant_error(curr_class) << "Cannot find object " << name << std::endl;
        type = Object;
    } else {
        type = *found_type;
    }
    
    return type;
}

