/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modularweb.persistence.common;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;

public class MorphiaClient {

	public static void main(String[] args) {
		DAO<Employee, ObjectId> dao = new BasicDAO<>(Employee.class, new MongoClient(), new Morphia(), "morphia");
		Employee rakesh = new Employee();
		rakesh.setName("Rakesh");
		rakesh.setSalary(Double.valueOf(5000000));
		dao.save(rakesh);
		final Employee daffy = new Employee("Daffy Duck", 40000.0);
		dao.save(daffy);
		
		final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
		dao.save(pepe);

		rakesh.getDirectReports().add(daffy);
		rakesh.getDirectReports().add(pepe);

		dao.save(rakesh);
		System.out.println("Saved!!");
		Query<Employee> createQuery = dao.createQuery();
		List<Employee> asList = createQuery.filter("id =", new ObjectId("5787bed42fe4c196084f8456")).asList();
		asList.forEach(emp -> {
			System.out.println(emp);
		});
	}
}
