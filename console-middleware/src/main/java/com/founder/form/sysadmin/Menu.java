package com.founder.form.sysadmin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Menu {

    private Long id;

    private String name;

    private String type;

    private String url;

    private String permission;

    private long level;

    private List<Menu> subMenus = new ArrayList<>();

    public Menu appendSubMenu(Menu menuInfo){
        subMenus.add(menuInfo);
        return this;
    }

}
