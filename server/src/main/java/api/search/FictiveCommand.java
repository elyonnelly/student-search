package api.search;

import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;

public class FictiveCommand extends RequestCommand implements Command<Query, List<List<Integer>> > {

    List<List<Integer>> result = new ArrayList<>();

    protected FictiveCommand(StudentSearchApp app) {
        super(app);
        result.add(new ArrayList<>()); //winnert
        result.add(new ArrayList<>());//prizewinner
        result.add(new ArrayList<>());//participant
        result.get(2).add(493076358);
        result.get(2).add(528087524);
        result.get(2).add(184094713);
        result.get(2).add(197211347);
        result.get(2).add(182402497);
    }


    @Override
    public void handleRequest(Query item) throws ClientException, ApiException {
        int c = 0;
        c++;
    }

    @Override
    public void businessLogic(Query item) {
        int c = 0;
        c++;
    }

    @Override
    public List<List<Integer>> getValue() {
        return result;
    }
}
