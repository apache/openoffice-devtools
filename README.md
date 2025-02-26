# Apache OpenOffice Developer Tools

Scripts that are helpful for developers who are building Apache OpenOffice from Source Code

## Contents

TODO

## History

This repository was cloned from a subversion repository.

1. Created using self-service at https://gitbox.apache.org/boxer/?action=newrepo

2. Cloned and pushed.

   ```bash
   # get the ASF authors map.
   curl https://gitbox.apache.org/authors.txt --output authors.txt
   # this clones the svn repository into git. You may need to install svn2git.
   # it takes almost three hours.
   git svn clone https://svn.apache.org/repos/asf/openoffice/devtools -A authors.txt
   cd devtools
   git remote add origin https://github.com/apache/openoffice-devtools.git
   git branch -M main
   # due to my setup I used GitHub Desktop for this next step.
   git push -u origin main
   ```
