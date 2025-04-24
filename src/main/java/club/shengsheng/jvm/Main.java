package club.shengsheng.jvm;

import java.util.List;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class Main {


    public static void main(String[] args) throws Exception {
        Hotspot hotspot = new Hotspot("club.shengsheng.project.Main", 
            List.of("/Users/gongxuanzhang/dev/github/a-project/target/classes"));
        hotspot.start();
    }
}
