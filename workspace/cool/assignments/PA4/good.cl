class A {
  pp() {
  "pepito"
  }
}
class C inherits A{
	a : Int;
	b : Bool;
	init(x : Int, y : Bool) : C {
           {
		a <- x;
		b <- y;
    p.lenght();
		self;
           }
	};

  foo(): Person {
    new Estudiante;
  }

};

class Person {
  asdf: C;
  init() {
    asdf <- new C; 
    asdf.pp();
  }
}
class  Estudiante inherits Person {}
class Profesor  inherits Person{}



Class Main {
	main():C {
	  (new C).init(1,true)
	};
};
