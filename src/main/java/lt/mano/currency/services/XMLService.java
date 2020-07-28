package lt.mano.currency.services;

import lt.mano.currency.generatedshemas.*;
import lt.mano.currency.model.Rate;
import org.springframework.stereotype.Service;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class XMLService {

    /**
     * Get currency data from xml to list by dates
     */
    public List<Rate> getFxRatesForCurrency(String currency, String fromDate, String toDate) {

        List<Rate> rates = new ArrayList<>();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FxRatesHandling.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0";

            URL url = new URL("https://www.lb.lt/webservices/FxRates/FxRates.asmx/"
                    + "getFxRatesForCurrency"
                    + "?tp=EU"
                    + "&ccy=" + currency
                    + "&dtFrom=" + fromDate
                    + "&dtTo=" + toDate

            );

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestProperty("User-Agent", userAgent);

            InputStream is = http.getInputStream();

            FxRatesHandling fxRatesHandling = (FxRatesHandling) jaxbUnmarshaller.unmarshal(is);


            for (FxRateHandling s : fxRatesHandling.getFxRate()) {
                Rate rate = new Rate();

                rate.setDate(s.getDt().toString());

                for (CcyAmtHandling s1 : s.getCcyAmt()) {
                    rate.setCurrency(s1.getCcy().toString());
                    rate.setRate(s1.getAmt().doubleValue());
                }
                rates.add(rate);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return rates;
    }

    /**
     * Get currency data from xml to list
     */
    public List<Rate> getCurrentFxRates() {

        List<Rate> rates = new ArrayList<>();

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(FxRatesHandling.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0";

            URL url = new URL("https://www.lb.lt/webservices/fxrates/FxRates.asmx/getCurrentFxRates?tp=eu");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestProperty("User-Agent", userAgent);

            InputStream is = http.getInputStream();

            FxRatesHandling fxRatesHandling = (FxRatesHandling) jaxbUnmarshaller.unmarshal(is);


            for (FxRateHandling s : fxRatesHandling.getFxRate()) {
                Rate rate = new Rate();

                rate.setDate(s.getDt().toString());

                for (CcyAmtHandling s1 : s.getCcyAmt()) {
                    rate.setCurrency(s1.getCcy().toString());
                    rate.setRate(s1.getAmt().doubleValue());
                }
                rates.add(rate);
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return rates;
    }

    /**
     * Get currency name list
     */
    public Map<String, String> getCurrencyList() {

        Map<String, String> currencyList = new HashMap<>();

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(CcyTblType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            URL url = new URL("https://www.lb.lt//webservices/fxrates/FxRates.asmx/getCurrencyList?");

            String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:78.0) Gecko/20100101 Firefox/78.0";

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestProperty("User-Agent", userAgent);

            InputStream is = http.getInputStream();

            CcyTblType ccyTblType = (CcyTblType) jaxbUnmarshaller.unmarshal(is);

            String code;
            String name = null;

            for (CcyNtryType s : ccyTblType.getCcyNtry()) {

                code = s.getCcy();
                for (CcyNmType s1 : s.getCcyNm()) {

                    if (s1.getLang().equals("EN")) {
                        name = s1.getValue();
                    }
                }
                currencyList.put(code, name.toUpperCase());
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return currencyList;
    }
}
