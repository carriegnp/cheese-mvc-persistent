package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping (value = "menu")
public class MenuController {

    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;


    @RequestMapping(value ="")
    public String index (Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menu of Cheeses");
        return "menu/index";
    }

    @RequestMapping (value ="add", method = RequestMethod.GET)
    public String displayAddForm (Model model) {
        model.addAttribute ("title","Add Menu");
        model.addAttribute (new Menu());
        return "menu/add";

    }

    @RequestMapping (value = "add", method = RequestMethod.POST)
    public String processAddForm (Model model, @ModelAttribute @Valid Menu menu, Errors errors){
        if (errors.hasErrors()) {
            model.addAttribute ("title","Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();

    }

    @RequestMapping (value ="view/{menuId}", method = RequestMethod.GET)
    public String viewMenu (Model model, @PathVariable int menuId) {

        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title", menu.getName());
        model.addAttribute("cheeses", menu.getCheeses());
        model.addAttribute("menuId", menu.getId());


        return "menu/view";

    }

    @RequestMapping (value = "add-item/{menuId}", method = RequestMethod.GET)
    public String DisplayAddItem (Model model,  @PathVariable int menuId) {
        Menu menu = menuDao.findOne(menuId);

        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("title", "Add item to menu:" + menu.getName());

        model.addAttribute("form", form);

        return "menu/add-item";
    }



    @RequestMapping (value = "add-item", method = RequestMethod.POST)
    public String ProcessAddItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {

        if (errors.hasErrors()) {

            //model.addAttribute("title", "Add item to menu:" );

           // model.addAttribute("form", form);

            return "menu/add-item/" ;
        }

        Cheese newCheese = cheeseDao.findOne(form.getCheeseId());
        Menu newMenu = menuDao.findOne(form.getMenuId());
        newMenu.addItem(newCheese);
        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId() ;

    }

}
