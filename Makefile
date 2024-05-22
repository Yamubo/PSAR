
JFLAGS = -g
JC = javac
JVM = java
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Serveur/Serveur.java \
		Client/Client.java

default: classes

classes: $(CLASSES:.java=.class)

client: 
	$(JVM) Client.Client

serveur: 
	$(JVM) Serveur.Serveur

clean:
	$(RM) */*.class