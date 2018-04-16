#!/bin/bash
# Bash script -- must have bash to run
# generates an index list for a directory based on filetypes desired
# last param is a title for the index

# usage: auto-index [dir] [filetype] [title]
if [ $# -eq 0 ]; then
	echo "Parameters: [directory to process] [filetypes to process] [title for index page]"
	echo "[directory to process is MANDATORY]"
exit;
fi
cd $1

rm index.html;

INDEX=`ls -1 *.$2 | sed "s/^.*/      <li\>\<a\ href=\"&\"\>&\<\\/a\>\<\\/li\>/"`
echo "<html>
  <head><title>Index of $3</title></head>
  <body>
    <h2>Index of $3</h2>
    <hr>
    <ui>
$INDEX
    <ui>
  </body>
</html>" > index.html;

