package com.tika.app2.Login;

public class Member {
    public Long ph;

    public Long getPh2() {
        return ph2;
    }

    public void setPh2(Long ph2) {
        this.ph2 = ph2;
    }

    public Long ph2;
    public int h;
    public int w;
    public String la;

    public String getLa() {
        return la;
    }

    public void setLa(String la) {
        this.la = la;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String lon;

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String n;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;






    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String bg ;
    public Member(Long ph)
    {
        this.ph=ph;
    }
    public Member()
    {}

    public Long getPh() {
        return ph;
    }

    public void setPh(Long ph) {
        this.ph = ph;
    }



//    public Member() {
//
//    }
//    public Long getPh(){
//        return ph;
//    }
//    public void setPh(Long ph){
//        this.ph=ph;
//    }
}