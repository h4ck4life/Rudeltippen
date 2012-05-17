package controllers;

import java.util.List;
import java.util.Map;

import models.Extra;
import models.ExtraTip;
import models.GameTip;
import models.Playday;
import models.User;
import play.mvc.With;
import utils.AppUtils;
import utils.ViewUtils;

@With(Auth.class)
public class Overview extends Root{
	public static void index(int number, String page) {
		if (number <= 0) { number = 1; }
		List<Playday> playdays = Playday.findAll();
		Playday playday = Playday.find("byNumber", number).first();

		final Map pagination = ViewUtils.getPagination("user", page, "/overview/playday/");
		final List<User> users = User.find("ORDER BY points DESC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));
		List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);

		render(playday, tips, playdays, number, pagination);
	}

	public static void playday(int number, String page) {
		if (number <= 0) { number = 1; }
		List<Playday> playdays = Playday.findAll();
		Playday playday = Playday.find("byNumber", number).first();

		final Map pagination = ViewUtils.getPagination("user", page, "/overview/playday/");
		final List<User> users = User.find("ORDER BY points DESC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));
		List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);

		render(playday, tips, playdays, number, pagination);
	}


	public static void extra(int number, String page) {
		final Map pagination = ViewUtils.getPagination("user", page, "/overview/extra/");
		final List<User> users = User.find("ORDER BY points DESC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));
		final List<Extra> extras = Extra.findAll();
		List<Map<User, List<ExtraTip>>> tips =  AppUtils.getExtraTips(users, extras);

		//FIX ME: We dont need a hardcoded playday
		Playday playday = new Playday();
		playday.setNumber(7);

		render(pagination, tips, playday, extras);
	}
}