package com.rubabuddin.nytimessearch.models;

import com.loopj.android.http.RequestParams;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static com.rubabuddin.nytimessearch.R.string.news_desk;

/**
 * Created by rubab.uddin on 10/21/2016.
 */
public class Query{

    private String queryStr;
    private int page;
    private String sortOrder;
    private String newsDeskFilters;
    private Calendar beginDate;
    private Calendar endDate;

        //on filter submit
        public Query(String queryString, int page, String sortOrder, String newsDeskFilters, Calendar beginDate, Calendar endDate) {
            this.queryStr = queryString;
            this.page = page;
            this.sortOrder = sortOrder;
            this.newsDeskFilters = newsDeskFilters;
            this.beginDate = beginDate;
            this.endDate = endDate;
        }
        //new query string search, no filters
        public Query(String queryString) {
            this.queryStr = queryString;
            this.page = 0;
            this.sortOrder = null;
            this.newsDeskFilters = null;
            this.beginDate = null;
            this.endDate = null;
        }
        //new query, with same filters applied
        public Query(Query query, String queryString){
            this.queryStr = queryString;
            this.page = 0;
            this.sortOrder = query.sortOrder;
            this.newsDeskFilters = query.newsDeskFilters;
            this.beginDate = query.beginDate;
            this.endDate = query.endDate;
        }
        //same query, with a new page of results (pagination)
        public Query(Query query, int page){
            this.queryStr = query.queryStr;
            this.page = page;
            this.sortOrder = query.sortOrder;
            this.newsDeskFilters = query.newsDeskFilters;
            this.beginDate = query.beginDate;
            this.endDate = query.endDate;
        }

    public String getQueryStr() {
        return queryStr;
    }

    public int getPage() {
        return page;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getNewsDeskFilters() {
        return newsDeskFilters;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }
    public Calendar getEndDate() {
        return beginDate;
    }

    private String formatDate(Calendar c) {
        String yearStr = String.valueOf(c.get(Calendar.YEAR));
        int month = c.get(Calendar.MONTH);
        String monthStr = String.valueOf(++month);
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        String dayStr = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        return yearStr + monthStr + dayStr;
    }

    public RequestParams getParams(String APIKey) {

        // initialize parameters holder
        RequestParams params = new RequestParams();
        params.put("api-key", APIKey);
        params.put("q", queryStr);

        if (sortOrder != null) {
            params.put("sort", sortOrder);
        }

        if (newsDeskFilters != null && !newsDeskFilters.equals("All")) {
            params.put("fq", "news_desk:\""+news_desk+"\"");
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        if (beginDate != null) {
            params.put("begin_date", format.format(beginDate.getTime()));
        }

        if (endDate != null) {
            params.put("end_date", format.format(endDate.getTime()));
        }

        if (newsDeskFilters != null && !newsDeskFilters.equals("All")) {
            params.put("fq", "news_desk:\""+news_desk+"\"");
        }

        params.put("page", page);

        return params;
    }
}