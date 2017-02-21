A multithreaded search engine app written in Java that takes in a root directory (local or url) through a breadth-first search approach goes through up to 50 files. In each file it stores every word, that word's position within the file, and the file's name where the word is found in, into an inverted index.

A user can access the search engine through the web and just like a standard search engine, can enter queries into a search box to get a list of appropriate links to files displayed. Through buttons the user may turn on or off: partial search, exact search, and private browsing.

The app also keeps track of user activity through cookies. As long as private browsing is disabled, cookies will be used to store the user's search history, viewed results, and likes. All of these can be viewed by the user as well as cleared.

When starting up the search engine app, the program accepts the following flags and values as command-line arguments in any order:

-dir directory where -dir indicates the next argument is a directory, and directory is the input directory of text files that must be processed

-index filepath where -index is an optional flag that indicates the next argument is a file path, and filepath is the path to the file to use for the inverted index output file. If the filepath argument is not provided, index.json will be used as the default output filename. If the -index flag is not provided, no output file will be produced.

-query filepath where -query indicates the next argument is a path to a text file of queries to be used for partial search. If this flag is not provided, then no partial search will be performed.

-exact filepath where -exact indicates the next argument is a path to a text file of queries to be used for exact search. If this flag is not provided, then no exact search will be performed.

-results filepath where -results is an optional flag that indicates the next argument is a file path, and filepath is the path to the file to use for the search results output file. This may include both partial and exact search results! If the filepath argument is not provided, results.json will be used as the default output filename. If the -results flag is not provided, no output file of search results will be provided, but the search operation will still be performed.

-url seed where -url indicates the next argument is the seed URL that must be processed and added to an inverted index data structure.

-multi threads where -multi indicates the next argument is the number of threads to use. If an invalid number of threads are provided, the program will default to 5 threads. If the -multi flag is not provided, then the program will be executed through a single thread.

-port num where -port indicates the next argument is the port the web server should use to accept socket connections. 8080 will be the default value if one is not provided.
