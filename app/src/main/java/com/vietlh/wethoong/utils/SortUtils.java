package com.vietlh.wethoong.utils;

import com.vietlh.wethoong.entities.Dieukhoan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by vietlh on 3/21/18.
 */

public class SortUtils {
    public ArrayList<Dieukhoan> sortBySortPoint(ArrayList<Dieukhoan> listDieukhoan, boolean isAscending) {
        final boolean itIsAscending = true;
        Collections.sort(listDieukhoan,new Comparator<Dieukhoan>() {
            @Override
            public int compare(Dieukhoan o1, Dieukhoan o2) {
                if (itIsAscending) {
                    return (o1.getSortPoint() > o2.getSortPoint() ? 1 : (o1.getSortPoint() < o2.getSortPoint() ? -1 : 0));
                }else {
                    return (o1.getSortPoint() > o2.getSortPoint() ? -1 : (o1.getSortPoint() < o2.getSortPoint() ? 1 : 0));
                }
            }
        });
        return listDieukhoan;
    }

    public ArrayList<Dieukhoan> sortByMatchCount(ArrayList<Dieukhoan> listDieukhoan, boolean isAscending) {
        final boolean itIsAscending = true;
        Collections.sort(listDieukhoan,new Comparator<Dieukhoan>() {
            @Override
            public int compare(Dieukhoan o1, Dieukhoan o2) {
                if (itIsAscending) {
                    return (o1.getMatchCount() > o2.getMatchCount() ? 1 : (o1.getMatchCount() < o2.getMatchCount() ? -1 : 0));
                }else {
                    return (o1.getMatchCount() > o2.getMatchCount() ? -1 : (o1.getMatchCount() < o2.getMatchCount() ? 1 : 0));
                }
            }
        });
        return listDieukhoan;
    }

    public ArrayList<Dieukhoan> sortByFirstAppearanceDistance(ArrayList<Dieukhoan> listDieukhoan, boolean isAscending) {
        final boolean itIsAscending = true;
        Collections.sort(listDieukhoan,new Comparator<Dieukhoan>() {
            @Override
            public int compare(Dieukhoan o1, Dieukhoan o2) {
                if (itIsAscending) {
                    return (o1.getFirstAppearanceDistance() > o2.getFirstAppearanceDistance() ? 1 : (o1.getFirstAppearanceDistance() < o2.getFirstAppearanceDistance() ? -1 : 0));
                }else {
                    return (o1.getFirstAppearanceDistance() > o2.getFirstAppearanceDistance() ? -1 : (o1.getFirstAppearanceDistance() < o2.getFirstAppearanceDistance() ? 1 : 0));
                }
            }
        });
        return listDieukhoan;
    }


    public ArrayList<Dieukhoan> sortByRelevent(ArrayList<Dieukhoan> listDieukhoan, String keyword) {
        if (listDieukhoan.isEmpty() || keyword.length() < 1 || listDieukhoan.size() > GeneralSettings.resultCountLimit) {
            return listDieukhoan;
        }

        String[] splittedKeyword = keyword.split(" ");
        int splittedKeywordCount = splittedKeyword.length;

        for (Dieukhoan dieukhoan : listDieukhoan) {
            String minhhoa = "";

            for (String mh :
                    dieukhoan.getMinhhoa()) {
                minhhoa += mh + " ";
            }

            String searchDetails = (dieukhoan.getSo() + " " + dieukhoan.getTieude() + " " + dieukhoan.getNoidung() + " " + minhhoa).trim().toLowerCase();
            boolean doneSet = false;

            for (int length = splittedKeywordCount; length >= 0; length--) {

                int end = (splittedKeywordCount - length);
                for (int start = 0; start <= end; start++) {
                    String key = "";
                    for (int i = start; i <= ((start + length) - 1); i++) {
                        key += splittedKeyword[i] + " ";
                    }
                    if (searchDetails.contains(key.trim())) {
                        dieukhoan.setMatchCount(length);
                        doneSet = true;
                        break;
                    }
                }
                if (doneSet) {
                    break;
                }
            }
        }
        return sortByMatchCount(listDieukhoan, false);
    }

    public ArrayList<Dieukhoan> sortByEarlyMatch(ArrayList<Dieukhoan> listDieukhoan, String keyword) {
        if (listDieukhoan.isEmpty() || keyword.length() < 1 || listDieukhoan.size() > GeneralSettings.resultCountLimit) {
            return listDieukhoan;
        }

        String[] splittedKeyword = keyword.split(" ");
        int splittedKeywordCount = splittedKeyword.length;

        for (Dieukhoan dieukhoan : listDieukhoan) {
            String minhhoa = "";

            for (String mh : dieukhoan.getMinhhoa()) {
                minhhoa += mh + " ";
            }

            String searchDetails = (dieukhoan.getSo() + " " + dieukhoan.getTieude() + " " + dieukhoan.getNoidung() + " " + minhhoa).trim().toLowerCase();
            boolean doneSet = false;

            for (int length = splittedKeywordCount; length >= 0; length--) {

                int end = (splittedKeywordCount - length);
                for (int start = 0; start <= end; start++) {
                    String key = "";
                    for (int i = start; i <= ((start + length) - 1); i++) {
                        key += splittedKeyword[i] + " ";
                    }
                    if (searchDetails.contains(key.trim())) {
                        int idx = searchDetails.indexOf(key);
                        dieukhoan.setFirstAppearanceDistance(idx);
                        doneSet = true;
                        break;
                    }
                }
                if (doneSet) {
                    break;
                }
            }
        }
        return sortByFirstAppearanceDistance(listDieukhoan, true);
    }

    public ArrayList<Dieukhoan> sortByBestMatch(ArrayList<Dieukhoan> listDieukhoan, String keyword) {
        ArrayList<Dieukhoan> sortedList = sortByRelevent(listDieukhoan, keyword.toLowerCase());
        sortedList = sortByEarlyMatch(sortedList,keyword.toLowerCase());

        if (!sortedList.isEmpty()) {
            Collections.sort(sortedList,new Comparator<Dieukhoan>() {
                @Override
                public int compare(Dieukhoan o1, Dieukhoan o2) {
                    return ((o1.getMatchCount() > o2.getMatchCount())
                            || ((o1.getMatchCount() == o2.getMatchCount()
                                && o1.getFirstAppearanceDistance() > o2.getFirstAppearanceDistance()))
                            ? 1 : (o1.getMatchCount() < o2.getMatchCount() ? -1 : 0));
                }
            });
        }
        return sortedList;
    }
}
