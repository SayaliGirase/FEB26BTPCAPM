package customer.bookstore.handlers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.bookservice.Books_;
import cds.gen.ordersservice.Orders;
import cds.gen.ordersservice.Orders_;
import cds.gen.ordersservice.OrderItems;
import cds.gen.ordersservice.OrderItems_;
import cds.gen.ordersservice.OrdersService_;
import cds.gen.sap.capire.bookstore.Books;

@Component
public class OrdersService implements EventHandler {
    @Autowired
    PersistenceService persistenceService;

    @Before(event = CqnService.EVENT_CREATE, entity = OrderItems_.CDS_NAME, service = OrdersService_.CDS_NAME)
    public void validateBookAndDecreaseStock(List<OrderItems> items){
        for(OrderItems item : items) {
            String bookId = item.getBookId();
            Integer amount = item.getAmount();
            
            //check if the book that is ordered, exists
            CqnSelect sel = Select.from(Books_.class).columns(b -> b.stock()).where(b -> b.ID().eq(bookId));
            Books book = persistenceService.run(sel).first(Books.class).orElseThrow(() -> new ServiceException(ErrorStatuses.NOT_FOUND, "Book does not exist."));

            //check if order could be fulfilled
            int stock = book.getStock();
            if (stock < amount){
                throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Not enough books in stock.");
            }

            //update the table book with new stock
            book.setStock(stock-amount);
            CqnUpdate update = Update.entity(Books_.class).data(book).where(b -> b.ID().eq(bookId));
            persistenceService.run(update);
        }
    }
    @Before(event = CqnService.EVENT_CREATE, entity = Orders_.CDS_NAME, service = OrdersService_.CDS_NAME)
    public void validateBookAndDecreaseStockViaOrders(List<Orders> orders){
        for(Orders order : orders){
            if(order.getItems() != null) {
                validateBookAndDecreaseStock(order.getItems());
            }
        }
    }
    
    @After(event = { CqnService.EVENT_CREATE, CqnService.EVENT_READ }, entity = OrderItems_.CDS_NAME, service = OrdersService_.CDS_NAME)
public void calculateNetAmount(List<OrderItems> items){
    for(OrderItems item : items){
        String bookId = item.getBookId();

        // Get the book that was ordered
        CqnSelect sel = Select.from(Books_.class).where(b -> b.ID().eq(bookId));
        Books book = persistenceService.run(sel).single(Books.class);

        // Calculate and set net amount
        item.setNetAmount(book.getPrice().multiply(new BigDecimal(item.getAmount())));
    }
}
    
    @After(event = { CqnService.EVENT_READ, CqnService.EVENT_CREATE}, entity = Orders_.CDS_NAME,service = OrdersService_.CDS_NAME)
    public void calculateTotal(List<Orders> orders) {
    for(Orders order : orders){

        // Step - 1 : Calculate net amount for expanded items
        if(order.getItems() != null) {
            calculateNetAmount(order.getItems());
        }

        // Step - 2 : Get all items of the order
        CqnSelect selItems = Select.from(OrderItems_.class).where(i -> i.parent().ID().eq(order.getId()));
        List<OrderItems> allItems = persistenceService.run(selItems).listOf(OrderItems.class);

        // Step - 3 : Calculate net amount of all items
        calculateNetAmount(allItems);

        // Step - 4 : Calculate and set the orders total
        BigDecimal total = new BigDecimal(0);
        for(OrderItems item : allItems) {
            total = total.add(item.getNetAmount());
        }

        // Add Comments | Refactor
        order.setTotal(total);
    }
}
}
