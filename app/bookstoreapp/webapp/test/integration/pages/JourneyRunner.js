sap.ui.define([
    "sap/fe/test/JourneyRunner",
	"bookstore/bookstoreapp/test/integration/pages/BooksList",
	"bookstore/bookstoreapp/test/integration/pages/BooksObjectPage",
	"bookstore/bookstoreapp/test/integration/pages/Books_textsObjectPage"
], function (JourneyRunner, BooksList, BooksObjectPage, Books_textsObjectPage) {
    'use strict';

    var runner = new JourneyRunner({
        launchUrl: sap.ui.require.toUrl('bookstore/bookstoreapp') + '/test/flpSandbox.html#bookstorebookstoreapp-tile',
        pages: {
			onTheBooksList: BooksList,
			onTheBooksObjectPage: BooksObjectPage,
			onTheBooks_textsObjectPage: Books_textsObjectPage
        },
        async: true
    });

    return runner;
});

