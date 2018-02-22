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

 ![Main perspective](http://i65.tinypic.com/219ytle.png)

 * Gallery perspective:

 ![Gallery perspective](http://i64.tinypic.com/210n2gz.png)

  * Authors view:

  ![Authors view](http://i68.tinypic.com/15yck0k.png)

  * Settings view:

  ![Settings view](http://i63.tinypic.com/xqjrjr.png)

  * Book edit view:

  ![Book view](http://i66.tinypic.com/2chu8h3.png)

# TODOs

* :thumbsup: bug gallery menu displays everywhere
* :thumbsup: error 50 on MacOS and random NPE's. Must be related to Spring data.
* :thumbsup: mark some books as "sold"
* :thumbsup: more fields in the book view area from the left
* :thumbsup: show resized version of attached images from book's documents
* :thumbsup: config for same view search or dedicated search view for the main perspective
* :thumbsup: special chars search should be possible by typing some of their "regular" chars (e.g. a instead of â, s instead of ș, etc)
* :thumbsup: errors management/view.