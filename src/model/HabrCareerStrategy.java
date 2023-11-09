package model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vo.Vacancy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerStrategy implements Strategy {

    private static final String URL_FORMAT = "https://career.habr.com/vacancies?q=java+%s&page=%d";
    @Override
    public List<Vacancy> getVacancies(String searchString) {
        int page = 0;
        List<Vacancy> allVacancies = new ArrayList<>();
        try {
            do {
                Document document = getDocument(searchString, page);

                //Elements vacanciesHtmlList = document.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");
                Elements vacanciesHtmlList = document.getElementsByClass("vacancy-card");

                System.out.println(vacanciesHtmlList.size());

                if (vacanciesHtmlList.isEmpty()) break;

                for (Element element: vacanciesHtmlList) {
                    Elements links = element.getElementsByClass("vacancy-card__title-link");
                    Elements locations = element.getElementsByClass("vacancy-card__meta");
                    Elements companyName = element.getElementsByClass("vacancy-card__company-title");
                    Elements salary = element.getElementsByClass("basic-salary");


                    Vacancy vacancy = new Vacancy();
                    vacancy.setSiteName("career.habr.com");
                    vacancy.setTitle(links.text());
                    vacancy.setUrl("https://career.habr.com" + links.attr("href"));
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
                .referrer("https://career.habr.com/")
                .get();
    }
}
