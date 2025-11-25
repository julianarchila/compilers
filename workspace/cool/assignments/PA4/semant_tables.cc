#include "semant.h"
#include "utilities.h"

// Implementations of ClassTable methods




ClassTable::ClassTable(Classes classes) : semant_errors(0) , error_stream(cerr) {
    
    install_basic_classes();

    // std::map<Symbol, Class_> ClassTable::m_classes
    // ==============================================
    // a map from Symbol to Class_

    // Let us build the inheritance graph and check for loops.
    SEMLOG << "Now building the inheritance graph:" << std::endl;

    // añadir las clases al map y verificar redeclaraciones
    for (int i = classes->first(); classes->more(i); i = classes->next(i)) {

        // class name cannot be SELF_TYPE
        if (classes->nth(i)->GetName() == SELF_TYPE) {
            semant_error(classes->nth(i)) << "Error! SELF_TYPE redeclared!" << std::endl;
        }

        if (m_classes.find(classes->nth(i)->GetName()) == m_classes.end()) {
            m_classes.insert(std::make_pair(classes->nth(i)->GetName(), classes->nth(i)));
        } else {
            semant_error(classes->nth(i)) << "Error! Class " << classes->nth(i)->GetName() << " has been defined!" << std::endl;
            return;
        }

    }

    // Verificamos que existe la clase Main
    if (m_classes.find(Main) == m_classes.end()) {
        semant_error() << "Class Main is not defined." << std::endl;
    }

    // Chequeos de herencia:
    // 1. Herede de una clase que exista
    // 2. No herede de si misma
    // 3. Que no herede de Int, Str, Bool o SELF_TYPE
    // 4. Que no haya ciclos en la herencia
    for (int i = classes->first(); classes->more(i); i = classes->next(i)) {

        curr_class = classes->nth(i);

        SEMLOG << "    " << curr_class->GetName();

        Symbol parent_name = curr_class->GetParent();
        while (parent_name != Object && parent_name != classes->nth(i)->GetName()) {

            // Ver que la clase padre exista en el map
            if (m_classes.find(parent_name) == m_classes.end()) {
                semant_error(curr_class) << "Error! Cannot find class " << parent_name << std::endl;
                return;
            }

            // ver que la clase no herede de Int, Str, Bool o SELF_TYPE
            if (parent_name == Int || parent_name == Str || parent_name == SELF_TYPE || parent_name == Bool) {
                semant_error(curr_class) << "Error! Class " << curr_class->GetName() << " cannot inherit from " << parent_name << std::endl;
                return;
            }

            SEMLOG << " <- " << parent_name;
            curr_class = m_classes[parent_name];
            parent_name = curr_class->GetParent();

        }

        if (parent_name == Object) {
            SEMLOG << " <- " << parent_name << std::endl;
        } else {
            semant_error(curr_class) << "Error! Cycle inheritance!" << std::endl;
            return;
        }

    }

    SEMLOG << std::endl;

}


// Verificar que a sea ancestro de b
bool ClassTable::CheckInheritance(Symbol a, Symbol b) {
    if (a == SELF_TYPE) {
        return b == SELF_TYPE;
    }

    if (b == SELF_TYPE) {
        b = curr_class->GetName();
    }

    for (; b != No_class; b = m_classes.find(b)->second->GetParent()) {
        if (b == a) {
            return true;
        }
    }
    return false;
}


// ClassTable::GetInheritancePath
// ==============================
// get a path from type to Object, inclusive
std::list<Symbol> ClassTable::GetInheritancePath(Symbol type) {
    if (type == SELF_TYPE) {
        type = curr_class->GetName();
    }

    std::list<Symbol> path;

    // note that Object's father is No_class
    for (; type != No_class; type = m_classes[type]->GetParent()) {
        path.push_front(type);  
    }

    return path;
}


// ClassTable::FindCommonAncestor
// ==============================
// find the first common ancestor of two types
Symbol ClassTable::FindCommonAncestor(Symbol type1, Symbol type2) {

    std::list<Symbol> path1 = GetInheritancePath(type1);
    std::list<Symbol> path2 = GetInheritancePath(type2);

    Symbol ret;
    std::list<Symbol>::iterator iter1 = path1.begin(),
                                iter2 = path2.begin();

    while (iter1 != path1.end() && iter2 != path2.end()) {
        if (*iter1 == *iter2) {
            ret = *iter1;
        } else {
            break;
        }

        iter1++;
        iter2++;
    }

    return ret;
}


// ClassTable::install_basic_classes
// =================================
// put Object, IO, Int, Bool, Str into ClassTable::m_classes
void ClassTable::install_basic_classes() {

    // The tree package uses these globals to annotate the classes built below.
   // curr_lineno  = 0;
    Symbol filename = stringtable.add_string("<basic class>");

    // The following demonstrates how to create dummy parse trees to
    // refer to basic Cool classes.  There's no need for method
    // bodies -- these are already built into the runtime system.

    // IMPORTANT: The results of the following expressions are
    // stored in local variables.  You will want to do something
    // with those variables at the end of this method to make this
    // code meaningful.

    // 
    // The Object class has no parent class. Its methods are
    //        abort() : Object    aborts the program
    //        type_name() : Str   returns a string representation of class name
    //        copy() : SELF_TYPE  returns a copy of the object
    //
    // There is no need for method bodies in the basic classes---these
    // are already built in to the runtime system.

    Class_ Object_class =
    class_(
        Object, 
        No_class,
        append_Features(
            append_Features(
                single_Features(method(cool_abort, nil_Formals(), Object, no_expr())),
                single_Features(method(type_name, nil_Formals(), Str, no_expr()))
            ),
            single_Features(method(copy, nil_Formals(), SELF_TYPE, no_expr()))
        ),
        filename
    );

    // 
    // The IO class inherits from Object. Its methods are
    //        out_string(Str) : SELF_TYPE       writes a string to the output
    //        out_int(Int) : SELF_TYPE            "    an int    "  "     "
    //        in_string() : Str                 reads a string from the input
    //        in_int() : Int                      "   an int     "  "     "
    //
    Class_ IO_class = 
    class_(
        IO, 
        Object,
        append_Features(
            append_Features(
                append_Features(
                    single_Features(method(out_string, single_Formals(formal(arg, Str)),
                        SELF_TYPE, no_expr())
                ),
                    single_Features(method(out_int, single_Formals(formal(arg, Int)),
                        SELF_TYPE, no_expr()))
                ),
                single_Features(method(in_string, nil_Formals(), Str, no_expr()))
            ),
            single_Features(method(in_int, nil_Formals(), Int, no_expr()))
        ),
        filename
    );  

    //
    // The Int class has no methods and only a single attribute, the
    // "val" for the integer. 
    //
    Class_ Int_class =
    class_(
        Int, 
        Object,
        single_Features(attr(val, prim_slot, no_expr())),
        filename
    );

    //
    // Bool also has only the "val" slot.
    //
    Class_ Bool_class =
    class_(Bool, Object, single_Features(attr(val, prim_slot, no_expr())), filename);

    //
    // The class Str has a number of slots and operations:
    //       val                                  the length of the string
    //       str_field                            the string itself
    //       length() : Int                       returns length of the string
    //       concat(arg: Str) : Str               performs string concatenation
    //       substr(arg: Int, arg2: Int): Str     substring selection
    //       
    Class_ Str_class =
    class_(
        Str, 
        Object,
        append_Features(
            append_Features(
                append_Features(
                    append_Features(
                        single_Features(attr(val, Int, no_expr())),
                        single_Features(attr(str_field, prim_slot, no_expr()))
                        ),
                    single_Features(method(length, nil_Formals(), Int, no_expr()))
                    ),
                single_Features(method(
                    concat, 
                    single_Formals(formal(arg, Str)),
                    Str, 
                    no_expr()
                    ))
                ),
            single_Features(method(
                substr, 
                append_Formals(
                    single_Formals(formal(arg, Int)), 
                    single_Formals(formal(arg2, Int))
                ),
                Str, 
                no_expr()
            ))
        ),
        filename
    );

    m_classes.insert(std::make_pair(Object, Object_class));
    m_classes.insert(std::make_pair(IO, IO_class));
    m_classes.insert(std::make_pair(Int, Int_class));
    m_classes.insert(std::make_pair(Bool, Bool_class));
    m_classes.insert(std::make_pair(Str, Str_class));

}

ostream& ClassTable::semant_error(Class_ c)
{                                 
    if (c == NULL)
        return semant_error();                         
    return semant_error(c->get_filename(),c);
}    

ostream& ClassTable::semant_error(Symbol filename, tree_node *t)
{
    error_stream << filename << ":" << t->get_line_number() << ": ";
    return semant_error();
}

ostream& ClassTable::semant_error()                  
{                                                 
    semant_errors++;                            
    return error_stream;
}

