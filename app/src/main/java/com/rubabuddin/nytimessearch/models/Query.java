package com.rubabuddin.nytimessearch.models;

import com.loopj.android.http.RequestParams;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by rubab.uddin on 10/21/2016.
 */
@Parcel
public class Query{

    String queryStr;
    int page;
    String sortOrder;

    public void setNewsDeskFilters(String newsDeskFilters) {
        this.newsDeskFilters = newsDeskFilters;
    }

    String newsDeskFilters;
    Calendar beginDate;
    Calendar endDate;

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

    //empty constructor needed for Parceler
    public Query(){
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

        if (newsDeskFilters != null && !newsDeskFilters.equals("All") && !newsDeskFilters.equals("")){
            params.put("fq", "news_desk:(" + getNewsDeskFilters() + ")");
        }

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        if (beginDate != null) {
            params.put("begin_date", format1.format(beginDate.getTime()));
            //Log.d("DEBUG", format.format(beginDate.getTime()));
        }

        if (endDate != null) {
            params.put("end_date", format1.format(endDate.getTime()));
        }

        params.put("page", page);

        return params;
    }
}