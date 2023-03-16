#The argument for the run command
ARG = $(arg)

COMPIL =	javac

EXEC =		java

EXEDIR =	build

SRC =		./*.java			\
			./Entities/*.java	\
			./Objects/*.java	\
			./Tools/*.java

.PHONY : run compil clean all

all: compil

compil:
	$(COMPIL) -d $(EXEDIR) $(SRC)

# The error comes because the Makefile is interrupted, it is not from the program
run:
	$(EXEC) -cp $(EXEDIR) Main $(ARG)

clean :
	rm output.dat
	rm -r $(EXEDIR)
