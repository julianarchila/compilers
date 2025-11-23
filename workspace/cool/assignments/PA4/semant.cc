#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <vector>
#include <list>
#include <set>
#include <sstream>
#include <string>
#include <cstring>
#include "semant.h"
#include "utilities.h"

static bool TESTING = false;
static std::ostringstream nop_sstream;
std::ostream &log = TESTING ? std::cout : nop_sstream;


extern int semant_debug;
extern char *curr_filename;

//////////////////////////////////////////////////////////////////////
//
// Symbols
//
// For convenience, a large number of symbols are predefined here.
// These symbols include the primitive type and method names, as well
// as fixed names used by the runtime system.
//
//////////////////////////////////////////////////////////////////////
Symbol 
    arg,
    arg2,
    Bool,
    concat,
    cool_abort,
    copy,
    Int,
    in_int,
    in_string,
    IO,
    length,
    Main,
    main_meth,
    No_class,
    No_type,
    Object,
    out_int,
    out_string,
    prim_slot,
    self,
    SELF_TYPE,
    Str,
    str_field,
    substr,
    type_name,
    val
;


Class_ curr_class = NULL;
ClassTable* classtable;
SymbolTable<Symbol, Symbol> attribtable;

std::map<Symbol, MethodTable> methodtables;

//
// Initializing the predefined symbols.
//
static void initialize_constants(void)
{
    arg         = idtable.add_string("arg");
    arg2        = idtable.add_string("arg2");
    Bool        = idtable.add_string("Bool");
    concat      = idtable.add_string("concat");
    cool_abort  = idtable.add_string("abort");
    copy        = idtable.add_string("copy");
    Int         = idtable.add_string("Int");
    in_int      = idtable.add_string("in_int");
    in_string   = idtable.add_string("in_string");
    IO          = idtable.add_string("IO");
    length      = idtable.add_string("length");
    Main        = idtable.add_string("Main");
    main_meth   = idtable.add_string("main");
    //   _no_class is a symbol that can't be the name of any 
    //   user-defined class.
    No_class    = idtable.add_string("_no_class");
    No_type     = idtable.add_string("_no_type");
    Object      = idtable.add_string("Object");
    out_int     = idtable.add_string("out_int");
    out_string  = idtable.add_string("out_string");
    prim_slot   = idtable.add_string("_prim_slot");
    self        = idtable.add_string("self");
    SELF_TYPE   = idtable.add_string("SELF_TYPE");
    Str         = idtable.add_string("String");
    str_field   = idtable.add_string("_str_field");
    substr      = idtable.add_string("substr");
    type_name   = idtable.add_string("type_name");
    val         = idtable.add_string("_val");
}

/*   This is the entry point to the semantic checker.
     Your checker should do the following two things:
     1) Check that the program is semantically correct
     2) Decorate the abstract syntax tree with type information
        by setting the `type' field in each Expression node.
        (see `tree.h')
     You are free to first do 1), make sure you catch all semantic
     errors. Part 2) can be done in a second stage, when you want
     to build mycoolc.
 */
void program_class::semant() {
    initialize_constants();

    // ClassTable constructor may do some semantic analysis
    classtable = new ClassTable(classes);

    if (classtable->errors()) {
        cerr << "Compilation halted due to static semantic errors." << endl;
        exit(1);
    }

    // Pass through every method in every class, construct the methodtables.
    log << "Now constructing the methodtables:" << std::endl;

    // std::map<Symbol, Class_> m_classes;
    for (std::map<Symbol, Class_>::iterator iter = classtable->m_classes.begin(); iter != classtable->m_classes.end(); ++iter) {
        log << "class " << iter->first << ":" << std::endl;

        Symbol class_name = iter->first;
        methodtables[class_name].enterscope();
        Features curr_features = classtable->m_classes[class_name]->GetFeatures();
        for (int j = curr_features->first(); curr_features->more(j); j = curr_features->next(j)) {
             Feature curr_feature = curr_features->nth(j);
             curr_feature->AddMethodToTable(class_name);
        }
    }

    // Now find illegal method overriding.
    log << "Now searching for illegal method overriding:" << std::endl;

    // Iterate over all classes
    for (std::map<Symbol, Class_>::iterator iter = classtable->m_classes.begin(); iter != classtable->m_classes.end(); ++iter) {
        
        // For some class, grab all its methods.
        Symbol class_name = iter->first;
        curr_class = classtable->m_classes[class_name];
        log << "    Consider class " << class_name << ":" << std::endl;

        Features curr_features = classtable->m_classes[class_name]->GetFeatures();

        for (int j = curr_features->first(); curr_features->more(j); j = curr_features->next(j)) {
            
            // We are checking one method of a class.
            Feature curr_method = curr_features->nth(j);

            if (curr_method->IsMethod() == false) {
                continue;
            }
            
            log << "        method " << curr_method->GetName() << std::endl;

            Formals curr_formals = ((method_class*)(curr_method))->GetFormals();
            
            std::list<Symbol> path = classtable->GetInheritancePath(class_name);
            // We are checking every method with the same name in the ancestors
            for (std::list<Symbol>::reverse_iterator iter = path.rbegin(); iter != path.rend(); ++iter) {
                
                Symbol ancestor_name = *iter;
                log << "            ancestor " << ancestor_name << std::endl;
                method_class* method = methodtables[ancestor_name].lookup(curr_method->GetName());
                
                if (method != NULL) {
                    // A method is found.
                    Formals formals = method->GetFormals();

                    int k1 = formals->first(), k2 = curr_formals->first();
                    for (; formals->more(k1) && curr_formals->more(k2); k1 = formals->next(k1), k2 = formals->next(k2)) {
                        if (formals->nth(k1)->GetType() != curr_formals->nth(k2)->GetType()) {
                            log << "error" << std::endl;
                            classtable->semant_error(classtable->m_classes[class_name]) << "Method override error: formal type not match." << std::endl;
                        }
                    }

                    if (formals->more(k1) || curr_formals->more(k2)) {
                        log << "error" << std::endl;
                        classtable->semant_error(classtable->m_classes[class_name]) << "Method override error: length of formals not match." << std::endl;
                    }
                }
            }
        }
    }

    log << std::endl;
    
    // Let's start checking all the types.
    log << "Now checking all the types:" << std::endl;

    for (int i = classes->first(); classes->more(i); i = classes->next(i)) {
        curr_class = classes->nth(i);

        log << "Checking class " << curr_class->GetName() << ":" << std::endl;

        // Get the inheritance path, add all the attributes.
        std::list<Symbol> path = classtable->GetInheritancePath(curr_class->GetName());
        for (std::list<Symbol>::iterator iter = path.begin(); iter != path.end(); iter++) {
            curr_class = classtable->m_classes[*iter];
            Features curr_features = curr_class->GetFeatures();
            attribtable.enterscope();
            for (int j = curr_features->first(); curr_features->more(j); j = curr_features->next(j)) {
                Feature curr_feature = curr_features->nth(j);
                curr_feature->AddAttribToTable(curr_class->GetName());
            }
        }
        
        curr_class = classes->nth(i);
        Features curr_features = curr_class->GetFeatures();

        // Check all features.
        for (int j = curr_features->first(); curr_features->more(j); j = curr_features->next(j)) {
            Feature curr_feature = curr_features->nth(j);
            curr_feature->CheckFeatureType();
        }

        for (int j = 0; j < path.size(); ++j) {
            attribtable.exitscope();
        }

        log << std::endl;
    }

    if (classtable->errors()) {
        cerr << "Compilation halted due to static semantic errors." << endl;
        exit(1);
    }

}
