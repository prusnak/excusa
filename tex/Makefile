NAME=excusa
EPSFILES=automaton1.eps automaton2.eps automaton3.eps automaton4.eps makefile.eps relationship.eps rpc-flow.eps rpc-model.eps uml-code.eps uml-grammar.eps uml-schema.eps uml-wsdl.eps usage.eps

all: $(NAME).pdf $(NAME).ps

$(NAME).pdf: rerebuild
	dvipdfm -p a4 $(NAME).dvi

$(NAME).ps: rerebuild
	dvips $(NAME).dvi

rerebuild: rebuild
	latex $(NAME).tex

rebuild: $(NAME).dvi
	latex $(NAME).tex

$(NAME).dvi: $(NAME).tex logo_mff.eps $(EPSFILES)
	latex $(NAME).tex

%.eps: %.dia
	dia $< -e $@ -t eps

%.eps: %.svg
	inkscape --export-bbox-page --without-gui --file=$< --export-eps=$@

clean:
	rm -f $(NAME).aux $(NAME).dvi $(NAME).lof $(NAME).log $(NAME).lot $(NAME).ps $(NAME).pdf $(NAME).toc $(EPSFILES)

mrproper: clean
	rm -f *~ *.backup
