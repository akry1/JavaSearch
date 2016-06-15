package search; /**
 * Created by aravind on 2/20/2016.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class Result{
    private ArrayList result;

    public Result(ArrayList result){
        this.result = result;
    }

    public ArrayList getResult(){
        return result;
    }
}


@RestController
public class SearchController {

    //http://localhost:8080/search?query=
    @CrossOrigin
    @RequestMapping("/search")
    public Result search(@RequestParam(value="query") String query) throws IOException, ParseException {
        return new Result(SimpleLuceneIndexing.runIndex(query));
    }
}

