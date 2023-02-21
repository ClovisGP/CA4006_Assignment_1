JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	InitBookstore.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class