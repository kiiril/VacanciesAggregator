package main;

import model.HabrCareerStrategy;
import model.Model;
import model.Provider;
import view.HtmlView;

public class Aggregator {
    public static void main(String[] args) {
        HtmlView view = new HtmlView();
        // add to model new Provider(new HHStrategy()) to parse from several sites
        Model model = new Model(view, new Provider(new HabrCareerStrategy()));
        Controller controller = new Controller(model);
        view.setController(controller);
        view.userCitySelectEmulationMethod();
    }
}
