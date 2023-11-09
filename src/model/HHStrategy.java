package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HHStrategy implements Strategy {
    private static final String URL_FORMAT = "https://hh.ru/search/vacancy?text=java+%s&page=%d" + "&items_on_page=20";

    @Override
    public List<Vacancy> getVacancies(String searchString) {
        int page = 0;
        List<Vacancy> allVacancies = new ArrayList<>();
        try {
            do {
                Document document = getDocument(searchString, page);

                //Elements vacanciesHtmlList = document.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
                Elements vacanciesHtmlList = document.getElementsByAttributeValue("data-qa","vacancy-serp__vacancy vacancy-serp__vacancy_standard_plus");
                if (vacanciesHtmlList.isEmpty()) break;

                for (Element element: vacanciesHtmlList) {
                    Elements links = element.getElementsByAttributeValue("data-qa", "serp-item__title");
                    Elements locations = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-address");
                    Elements companyName = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-employer");
                    Elements salary = element.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy-compensation");

                    Vacancy vacancy = new Vacancy();
                    vacancy.setSiteName("hh.ru");
                    vacancy.setTitle(links.text());
                    vacancy.setUrl(links.attr("href"));
                    vacancy.setCity(locations.get(0).text());
                    vacancy.setCompanyName(companyName.get(0).text());
                    vacancy.setSalary(salary.size() > 0 ? salary.get(0).text() : "");

                    allVacancies.add(vacancy);
                }
                page++;
            } while (true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allVacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException {
        return Jsoup.connect(String.format(URL_FORMAT, searchString, page))
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
                .referrer("https://hh.ru/")
                .get();
    }
}
