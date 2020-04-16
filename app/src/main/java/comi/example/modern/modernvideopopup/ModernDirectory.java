package comi.example.modern.modernvideopopup;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Instafeed2 on 10/24/2019.
 */

public class ModernDirectory {
    String name;
    String id;
    String size;
    public ArrayList<ModernFiles> insideData =new ArrayList<>();


    public ModernDirectory(String name, String id, String size, ArrayList<ModernFiles> insideData) {
        this.name = name;
        this.id = id;
        this.size = size;
    }

    public ModernDirectory(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getSize() {
        return Integer.toString(insideData.size());
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static Comparator<ModernDirectory> DirectoryNameComparator = new Comparator<ModernDirectory>() {

        public int compare(ModernDirectory s1, ModernDirectory s2) {
            String ModernDirectoryName1 = s1.getName().toUpperCase();
            String ModernDirectoryName2 = s2.getName().toUpperCase();

            //ascending order
            return ModernDirectoryName1.compareTo(ModernDirectoryName2);

            //descending order
            //return ModernDirectoryName2.compareTo(ModernDirectoryName1);
        }};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ModernFiles> getInsideData() {
        return insideData;
    }

    public void setInsideData(ArrayList<ModernFiles> insideData) {
        this.insideData.addAll(insideData);
    }

    public void addFile(ModernFiles data )
    {
        insideData.add(data);
    }
}
