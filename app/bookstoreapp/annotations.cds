using BookService as service from '../../srv/services';
annotate service.Books with @(

    UI.SelectionFields : [
        title,
        author,
        price,
        currency_code
    ],

    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : '{i18n>TITLE}'
        },
        {
            $Type : 'UI.DataField',
            Value : descr,
            Label : '{i18n>DESCR}'
        },
        {
            $Type : 'UI.DataField',
            Value : category_ID,
            Label : '{i18n>CAT_ID}'
        },
        {
            $Type : 'UI.DataField',
            Value : stock,
            Label : '{i18n>STOCK}'
        },
        {
            $Type : 'UI.DataField',
            Value : price,
            Label : '{i18n>PRICE}'
        },
        {
            $Type : 'UI.DataField',
            Value : CDES,
            Label : '{i18n>CURRENCY}'
        }
    ],
        UI.HeaderInfo : {
        TypeName : 'Book',
        TypeNamePlural : 'Book Details',
        Title : {
            $Type : 'UI.DataField',
            Value : title
        },
        Description : {
            $Type : 'UI.DataField',
            Label : 'Description',
            Value : category_ID
        },
        ImageUrl : 'https://i2.pngimg.com/pngimg/s89/592/png-clipart-capgemini-septi-engineering-information-industry-blockchain-miscellaneous-blue.png'
    },
UI.Facets : [
    {
        $Type : 'UI.CollectionFacet',
        Label : 'Book Details',
        Facets : [
            {
                $Type : 'UI.ReferenceFacet',
                Label : 'Book Information',
                Target : '@UI.FieldGroup#BookDetails'
            },
            {
                $Type : 'UI.ReferenceFacet',
                Label : 'Stock & Price Details',
                Target : '@UI.FieldGroup#StockPrice'
            }
        ]
    }
],

UI.FieldGroup #BookDetails : {
    $Type : 'UI.FieldGroupType',
    Data : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : 'Book Title'
        },
        {
            $Type : 'UI.DataField',
            Value : descr,
            Label : 'Book Description'
        }
 ]
},

UI.FieldGroup #StockPrice : {
    $Type : 'UI.FieldGroupType',
    Data : [
        {
            $Type : 'UI.DataField',
            Value : stock,
            Label : 'Available Stock'
        },
        {
            $Type : 'UI.DataField',
            Value : price,
            Label : 'Price'
        },
        {
            $Type : 'UI.DataField',
            Value : CDESC
        }
    ]
}

);