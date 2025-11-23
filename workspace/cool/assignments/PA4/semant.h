#ifndef SEMANT_H_
#define SEMANT_H_

#include <assert.h>
#include <iostream>  
#include "cool-tree.h"
#include "stringtab.h"
#include "symtab.h"
#include "list.h"
#include <map>
#include <list>
#include <vector>
#include <set>

#define TRUE 1
#define FALSE 0

class ClassTable;
typedef ClassTable *ClassTableP;

// Typedefs moved from semant.cc
typedef SymbolTable<Symbol, method_class> MethodTable;

// Global symbols used across semantic analysis
extern Symbol arg, arg2, Bool, concat, cool_abort, copy, Int, in_int, in_string, IO, length, Main, main_meth, No_class, No_type, Object, out_int, out_string, prim_slot, self, SELF_TYPE, Str, str_field, substr, type_name, val;

// Global state
extern Class_ curr_class;
extern ClassTable* classtable;
extern SymbolTable<Symbol, Symbol> attribtable;
extern std::map<Symbol, MethodTable> methodtables;

// Simple compile-time / run-time toggled semantic debug logging.
// Toggle by changing the initializer of kSemantLog in semant.cc.
extern bool kSemantLog;
#define SEMLOG if (kSemantLog) std::cout


// This is a structure that may be used to contain the semantic
// information such as the inheritance graph.  You may use it or not as
// you like: it is only here to provide a container for the supplied
// methods.

class ClassTable {
private:
	int semant_errors;
	void install_basic_classes();
	ostream& error_stream;
	
public:
	std::map<Symbol, Class_> m_classes;
	ClassTable(Classes);
	int errors() { return semant_errors; }
	ostream& semant_error();
	ostream& semant_error(Class_ c);
	ostream& semant_error(Symbol filename, tree_node *t);

	// These methods are not in the starting code.
	bool CheckInheritance(Symbol ancestor, Symbol child);
	Symbol FindCommonAncestor(Symbol type1, Symbol type2);
	std::list<Symbol> GetInheritancePath(Symbol type);
};


#endif
