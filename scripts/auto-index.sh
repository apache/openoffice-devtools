#!/bin/bash
# Bash script -- must have bash to run
# generates an index list for a directory based on filetypes desired
# last param is a title for the index

# usage: auto-index [dir] [filetype] [title]
if [ $# -eq 0 ]; then
	echo "Parameters: [directory to process] [filetypes to process] [title for index page]"
	echo "[directory to process] is mandatory"
	echo "No parameters given: using current directory with html files and default title"
fi
FILETYPE="html"
TITLE=$1
if [ $2 ]; then FILETYPE = $2;  fi
if [ $3 ]; then TITLE = $3;  fi

cd $1
echo "Now in " $1 
read -p "OK?..."

rm -f index.html

# Header info
# begin new index page
echo "
 <html>
  <head><title>Index of ${TITLE}</title></head>
  <body>
    <h2>Index of ${TITLE}</h2>
    <hr> " > TOP
echo "Done with HEADER"


# pick up directory entries first into DIR_INDEX array

DIR_INDEX=$(ls -1 -d */ | sed -r "s/(^.*)\/$/<li\>\<a href=\"\1\">\1\<\\/a>\<\\/li\>/")
echo "Done with DIR_INDEX"


if [ "$DIR_INDEX" != "" ]; then
echo "<ul style=\"list-style-type:square; font-size: 120%; font-weight:bold; text-transform:uppercase;\">
	${DIR_INDEX}
</ul>" >> TOP
 
fi


FILES=""
# process plain html files in their own sub list
FILES=`ls -1 *.${FILETYPE} 2>/dev/null | sed -r "s/^.*/      <li\>\<a\ href=\"&\"\>&\<\\/a\>\<\\/li\>/"`

if [ "$FILES" != "" ]; then
# print 
echo "
<ul>
	 ${FILES} 
</ul>" > BOTTOM
fi



echo "
  </body>
</html>" >> BOTTOM

#Prepend header and dir info 
TOP=${HEADER}${DIR_INDEX}

cat TOP BOTTOM >> index.html 

rm -f TOP BOTTOM







