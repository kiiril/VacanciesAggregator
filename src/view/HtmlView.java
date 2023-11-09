package view;

import main.Controller;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vo.Vacancy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class HtmlView implements View {
    private Controller controller;
    private final String filePath = "src/" + this.getClass().getPackage().getName().replaceAll("\\.", "/") + "/vacancies.html";
    @Override
    public void update(List<Vacancy> vacancies) {
        try {
            String newContent = getUpdatedFileContent(vacancies);
            updateFile(newContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;    
    }

    public void userCitySelectEmulationMethod() {
        controller.onCitySelect("Moscow");
    }

    private String getUpdatedFileContent(List<Vacancy> vacancies) {
        try {
            Document doc = getDocument();
            Elements templateHidden = doc.getElementsByClass("template");
            Element template = templateHidden.clone().removeAttr("style").removeClass("template").get(0);

            Elements prevVacancies = doc.getElementsByClass("vacancy");
            for (Element redundant: prevVacancies) {
                if (!redundant.hasClass("template")) {
                    redundant.remove();
                }
            }

            for (Vacancy vacancy: vacancies) {
                Element vacancyElement = template.clone();
                
                Element city = vacancyElement.getElementsByClass("city").get(0);
                city.appendText(vacancy.getCity());

                Element companyName = vacancyElement.getElementsByClass("companyName").get(0);
                companyName.appendText(vacancy.getCompanyName());

                Element salary = vacancyElement.getElementsByClass("salary").get(0);
                salary.appendText(vacancy.getSalary());

                Element reference = vacancyElement.getElementsByAttribute("href").get(0);
                reference.attr("href", vacancy.getUrl());
                reference.appendText(vacancy.getTitle());

                templateHidden.before(vacancyElement.outerHtml());
            }
            return doc.html();
        } catch (IOException e) {
            e.printStackTrace();
            return "Some exception occurred";
        }
    }

    private void updateFile(String fileContent) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(fileContent, 0, fileContent.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Document getDocument() throws IOException {
        return Jsoup.parse(new File(filePath), "UTF-8");
    }
}
