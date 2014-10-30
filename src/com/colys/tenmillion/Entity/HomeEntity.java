package com.colys.tenmillion.Entity;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HomeEntity {
	public int NormalMember = 0;
	public int AskForLeave = 0;
	public int NotBack = 0;
	public LinkedList<Member> MemberMap;
	public LinkedList<MonthPlan> PlanList;
	public LinkedList<PeopleComing> PeopleComingList;
	public LinkedList<Task> TaskList;
	public LinkedList<AskForLeave> UnBackMembers;
	
	
	 public static HomeEntity FromJson(String json) {
         Gson gson=new Gson();
         return gson.fromJson(json, HomeEntity.class);
	 }
	
	 public static LinkedList<HomeEntity> ListFromJson(String json) {
	        Type listType = new TypeToken<LinkedList<HomeEntity>>(){}.getType();
	        Gson gson=new Gson();
	        return gson.fromJson(json, listType);
	 }
}
