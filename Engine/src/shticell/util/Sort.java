package shticell.util;

import dto.SheetDto;
import shticell.sheet.api.Sheet;
import shticell.sheet.cell.api.Cell;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.impl.SheetImpl;
import shticell.sheet.range.Range;

import java.util.*;
import java.util.stream.Collectors;

public interface Sort {

    class keyColInfo implements Comparator<keyColInfo> {
        private  int row;
        private Double value;

        public keyColInfo(int row, Double value) {
            this.row = row;
            this.value = value;
        }


        public int getRow(){return row;}
        public Double getValue(){return value;}
        public void setValue(Double value){
            this.value =value;
        }

        @Override
        public int compare(keyColInfo o1, keyColInfo o2) {
            if(o1.getValue() == o2.getValue()){
                return 0;
            }
            else if(o1.getValue() < o2.getValue()){
                return -1;
            }
            return 1;
        }
    }
    public static SheetDto getSortedSheetDTO(Sheet sheet, String from, String to, String...keyColumns) {
        Coordinate start = CoordinateFactory.getCoordinate(from);
        Coordinate end = CoordinateFactory.getCoordinate(to);
        List<keyColInfo> toSortList = new ArrayList<>();
        for(int i = start.row(); i<=end.row(); i++){
            toSortList.add(new keyColInfo(i, sheet.GetCell(CoordinateFactory.getCoordinate(keyColumns[0]+i)).GetEffectiveValue().getValueWithExpectation(Double.class)));
        }
        recursiveSort(toSortList,sheet,0,keyColumns);
        Map<Coordinate, Cell> cellsRes = new HashMap<>();
        List<Coordinate> coordinatesInRange = new Range("",start,end).getRangeCellsCoordinate();
        return null;
    }
    public static void recursiveSort(List <keyColInfo> toSortList,Sheet sheet, int keyind,String ... keyColums){
        toSortList = toSortList.stream().sorted((c1,c2)->c1.compare(c1,c2)).collect(Collectors.toList());
        List<String> buckets = listOfReapts(toSortList);
        if(keyind < keyColums.length-1) {
            for (String bucket : buckets) {
                Integer sind = Integer.parseInt(bucket.split(",")[0]);
                Integer eind = Integer.parseInt(bucket.split(",")[1]);
                List<keyColInfo> subSort = toSortList.subList(sind, eind);
                for (int i = sind; i <= eind; i++) {
                    toSortList.get(i).setValue(sheet.GetCell(CoordinateFactory.getCoordinate(keyColums[keyind+1]+toSortList.get(i).getRow()))
                            .GetEffectiveValue().getValueWithExpectation(Double.class));
                }
                recursiveSort(subSort,sheet,keyind+1,keyColums);
            }
        }




    }

    public static  List<String>  listOfReapts(List<keyColInfo> checklist){
        boolean flag = false;
        Double startval = checklist.getFirst().getValue();
        List<String> res = new ArrayList<>();
        int sind =0;
        int eind = 0;
        for(int i=1; i<checklist.size(); i++){
            if(checklist.get(i).getValue() == startval){
                eind = i;
            }
            else{
                if(sind<eind){
                    res.add(sind+","+eind);
                }
                sind = i;
                eind = i;
                startval = checklist.get(i).getValue();
            }
        }
        return res;
    }


}



