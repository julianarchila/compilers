class Main inherits IO{
    main(): Object {
        let s: Stack <- (new Stack) in {
            s.push("A");
            s.push("B");
            s.push("C");
            out_string(s.tostring().concat("\n"));

            while ( not (s.getsize() = 0)) loop
                {
                    out_string(s.tostring().concat("\n"));
                    s.pop();
                }
            pool;
            s;
        }
    };
};

class List inherits A2I {
    item: Object;
    next: List;

    init(v: Object, n: List): List {
        {
            item <- v;
            next <- n;
            self;
        }
    };

    getitem(): Object{
        item
    };

    getnext(): List {
        next
    };

    flatten(): String {
        let string: String <-
            case item of
                i: Int => i2a(i);
                s: String => s;
                o: Object => {abort(); "";};

            esac

        in
            if (isvoid next) then
                string
            else
                string.concat(" ").concat(next.flatten())
            fi

    };

};

class Stack {
    top: List;
    size: Int;

    push(v: Object): SELF_TYPE {

        let newNode: List <-  (new List).init(v, top) in {
            top <- newNode;
            size <- size + 1;
            self;
        }

    };

    pop(): SELF_TYPE {
        if (isvoid top) then
            self
        else
            {
                top <- top.getnext();
                size <- size - 1;
                self;
            }
        fi
    };

    getsize(): Int {
        size
    };

    tostring(): String {
        if (isvoid top) then
            ""
        else
            top.flatten()
        fi
    };

};