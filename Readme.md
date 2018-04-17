# Books Manager SWT

Having manny books, it's always a good idea to have some king of ordering system for them
This is exactly what this app does.

# Some features:

 * over 20 ways of grouping the books (by title, author, price, cover type, location, readers, etc)
 * customizable pagination for the main grid
 * gallery view, with book covers and book ratings
 * multi-user support
 * ability to import/export books, authors and book images
 * some reports available
 * application settings
 * random book pick-up!
 * ability to enter quotes, documents, notes, chapter list, characters and book review.
 * logs manager
 * exports manager
 * app users manager
 * some validation algorithms (as a bonus :) )
 * login/logout mechanism
 * different notification styles.
 * Windows/MacOS support and testing. Should work on Linux as well, but it was not tested.
 * search highlight and/or live filtering

Startup parameters:

-XstartOnFirstThread -Duser.timezone="UTC"

# Some technologies:

 * Spring boot
 * Mongo database
 * SWT
 * maven
 * Jface
 * iText
 * jExcel
 * Nebula widgets

# Screenshots

 * Main perspective:

 ![Main perspective](http://i64.tinypic.com/210n2gz.png)

 * Gallery perspective:

 ![Gallery perspective](http://i65.tinypic.com/219ytle.png)

  * Authors view:

  ![Authors view](http://i68.tinypic.com/15yck0k.png)

  * Settings view:

  ![Settings view](http://i63.tinypic.com/xqjrjr.png)

  * Book edit view:

  ![Book view](http://i66.tinypic.com/2chu8h3.png)

# How to launch in standalone mode

* ```mvn clean install -DskipTests```
* create a new .sh file (let's say ```books.sh```) with the following content:

        #!/bin/bash
        java -jar -XstartOnFirstThread -Duser.timezone="UTC" ~/.m2/repository/com/papao/books/books/0.0.1-SNAPSHOT/books-0.0.1-SNAPSHOT.jar
* execute the sh with:

        ./books.sh
* if necessary, change the permissions on the sh file with:

        sudo chmod +x books.sh

# Setup
Unfortunately, the application requires some manually installed maven dependencies as well. All those deps are in the /libs folder.
For easy install, execute the following commands from the application root (make sure to adapt pom.xml to match your 
current OS). I have no idea why, but some strange maven errors occures when installing all the entries from below at once.
Installing them one by one had no issue.

        mvn install:install-file -Dfile=libs/org.eclipse.jface/org.eclipse.core.commands_3.5.0.I20090525-2000.jar -Dsources=libs/org.eclipse.jface/source/org.eclipse.core.commands.source_3.5.0.I20090525-2000.jar -DgroupId=org.eclipse.core -DartifactId=commands -Dversion=3.5.0.I20090525-2000 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.jface/org.eclipse.core.runtime_3.6.0.v20091204.jar -Dsources=libs/org.eclipse.jface/source/org.eclipse.core.runtime.source_3.6.0.v20091204.jar -DgroupId=org.eclipse.core -DartifactId=runtime -Dversion=3.6.0.v20091204 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.jface/org.eclipse.equinox.common_3.6.0.v20091203.jar -Dsources=libs/org.eclipse.jface/source/org.eclipse.equinox.common.source_3.6.0.v20091203.jar -DgroupId=org.eclipse.equinox -DartifactId=common -Dversion=3.6.0.v20091203 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.jface/org.eclipse.jface_3.6.0.I20091207-1800.jar -Dsources=libs/org.eclipse.jface/source/org.eclipse.jface.source_3.6.0.I20091207-1800.jar -DgroupId=org.eclipse -DartifactId=jface -Dversion=3.6.0.I20091207-1800 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/swt/current/mac/swt-debug.jar -Dsources=libs/swt/current/mac/src.zip -DgroupId=org.eclipse -DartifactId=swt -Dversion=4.7.3a-cocoa-macosx -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/swt/current/x86/swt-debug.jar -Dsources=libs/swt/current/x86/src.zip -DgroupId=org.eclipse -DartifactId=swt -Dversion=4.7.3a-x86 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/swt/current/x64/swt-debug.jar -Dsources=libs/swt/current/x64/src.zip -DgroupId=org.eclipse -DartifactId=swt -Dversion=4.7.3a-x64 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/borg_images.jar -DgroupId=com.papao -DartifactId=borg_images -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.aspen.cloud.widgets/aspencloud.widgets.jar -Dsources=libs/org.aspen.cloud.widgets/aspencloud.widgets.sources.zip -DgroupId=org.aspen.cloud -DartifactId=widgets -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.nebula.widgets/org.eclipse.nebula.widgets.datechooser_1.0.0.HEAD.jar -DgroupId=org.eclipse.nebula.widgets -DartifactId=datechooser -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.nebula.widgets/org.eclipse.nebula.widgets.formattedtext_1.0.0.HEAD.jar -DgroupId=org.eclipse.nebula.widgets -DartifactId=formattedtext -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.nebula.widgets/org.eclipse.nebula.widgets.pshelf_1.0.0.HEAD.jar -DgroupId=org.eclipse.nebula.widgets -DartifactId=pshelf -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.nebula.widgets/org.eclipse.nebula.widgets.pgroup_1.0.0.HEAD.jar -DgroupId=org.eclipse.nebula.widgets -DartifactId=pgroup -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/org.eclipse.nebula.widgets/org.eclipse.nebula.widgets.tablecombo_1.0.0.HEAD.jar -DgroupId=org.eclipse.nebula.widgets -DartifactId=tablecombo -Dversion=1.0 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/tika-core-1.14.jar -DgroupId=org.apache.tika -DartifactId=tika-core -Dversion=1.14 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/opal/opal-0.9.5.2.jar -DgroupId=org.mihalis.opal -DartifactId=opal -Dversion=0.9.5.2 -Dpackaging=jar -Dsources=libs/opal/opal-0.9.5-src.zip;
        mvn install:install-file -Dfile=libs/org.eclipse.ui.forms-3.5.jar -DgroupId=org.eclipse.ui -DartifactId=forms -Dversion=3.5 -Dpackaging=jar;
        mvn install:install-file -Dfile=libs/com.inamik.utils.tableformatter-0.96.0/com.inamik.utils.tableformatter-0.96.0.jar -Dsources=libs/com.inamik.utils.tableformatter-0.96.0/com.inamik.utils.tableformatter-0.96.0.sources.zip -DgroupId=com.inamik.utils -DartifactId=tableformatter -Dversion=0.96.0 -Dpackaging=jar;

POM dependencies related to manually installed files:

        <dependency>
            <groupId>org.eclipse</groupId>
            <artifactId>swt</artifactId>
            <version>4.7-cocoa-macosx</version>
            <!--<version>4.7-x86</version>-->
            <!--<version>4.7-x64</version>-->
        </dependency>
        
        <!-- jface 3.6 -->
        <dependency>
             <groupId>org.eclipse.core</groupId>
             <artifactId>commands</artifactId>
             <version>3.5.0.I20090525-2000</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.core</groupId>
             <artifactId>runtime</artifactId>
             <version>3.6.0.v20091204</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.equinox</groupId>
             <artifactId>common</artifactId>
             <version>3.6.0.v20091203</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse</groupId>
             <artifactId>jface</artifactId>
             <version>3.6.0.I20091207-1800</version>
        </dependency>
        
        <!-- borg images -->
        <dependency>
             <groupId>com.papao</groupId>
             <artifactId>borg_images</artifactId>
             <version>1.0</version>
        </dependency>
        
        <!-- aspencloud widgets -->
        <dependency>
             <groupId>org.aspen.cloud</groupId>
             <artifactId>widgets</artifactId>
             <version>1.0</version>
        </dependency>
        
        <!-- nebula widgets -->
        
        <dependency>
             <groupId>org.eclipse.nebula.widgets</groupId>
             <artifactId>datechooser</artifactId>
             <version>1.0</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.nebula.widgets</groupId>
             <artifactId>formattedtext</artifactId>
             <version>1.0</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.nebula.widgets</groupId>
             <artifactId>pshelf</artifactId>
             <version>1.0</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.nebula.widgets</groupId>
             <artifactId>pgroup</artifactId>
             <version>1.0</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.nebula.widgets</groupId>
             <artifactId>tablecombo</artifactId>
             <version>1.0</version>
        </dependency>
        <dependency>
             <groupId>org.eclipse.ui</groupId>
             <artifactId>forms</artifactId>
             <version>3.5</version>
        </dependency>
        <dependency>
             <groupId>com.inamik.utils</groupId>
             <artifactId>tableformatter</artifactId>
             <version>0.96.0</version>
        </dependency>

Startup parameters:
    
        -XstartOnFirstThread -Duser.timezone="UTC"

# TODOs

* :thumbsup: bug gallery menu displays everywhere
* :thumbsup: error 50 on MacOS and random NPE's. Must be related to Spring data.
* :thumbsup: mark some books as "sold"
* :thumbsup: more fields in the book view area from the left
* :thumbsup: show resized version of attached images from book's documents
* :thumbsup: config for same view search or dedicated search view for the main perspective
* :thumbsup: special chars search should be possible by typing some of their "regular" chars (e.g. a instead of â, s instead of ș, etc)
* :thumbsup: errors management/view.
* :thumbsup: multi-language app
* :thumbsup: upgrade all dependencies to their latest versions