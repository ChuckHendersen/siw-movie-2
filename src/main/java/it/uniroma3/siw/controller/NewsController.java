package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import it.uniroma3.siw.model.News;
import it.uniroma3.siw.repository.NewsRepository;

@Controller
public class NewsController {
	@SuppressWarnings("unused")
	@Autowired
	private NewsRepository newsRepository;
	
	public String formNewNews(Model model) {
		model.addAttribute("news", new News());
		return "formNewNews.html";
	}
}
