all:
doc=main

all: $(doc).tex
	pdflatex $(doc).tex
	bibtex $(doc).aux
	pdflatex $(doc).tex
	pdflatex $(doc).tex

clean :
	rm -f $(doc).log
	rm -f $(doc).aux
	rm -f $(doc).dvi
	rm -f $(doc).pdf
	rm -f $(doc).toc
	rm -f $(doc).lof
	rm -f $(doc).lot
	rm -f $(doc).bbl
	rm -f $(doc).blg
	rm -f $(doc).out
