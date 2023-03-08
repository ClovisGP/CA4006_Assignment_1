COMPIL =	javac

EXEC =		java

EXEDIR =	./build

SRC =		./*.java			\
			./Entities/*.java	\
			./Objects/*.java	\
			./Tools/*.java

.PHONY : run compil clean all

all: compil

compil:
	$(COMPIL) -d $(EXEDIR) $(SRC)

# The error comes because the Makefile is interrupt, it is not from the program
run:
	$(EXEC) -cp $(EXEDIR) Main

clean :
	rm -f *.class $(EXEDIR)
