import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by tdonohue on 13/02/2018.
 */
public class MyProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        exchange.getIn().setHeader("MyValue", 12345);
    }
}
