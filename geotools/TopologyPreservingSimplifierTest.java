package kr.newlayer.lxsdq.sdq.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class TopologyPreservingSimplifierTest {
	
	public static void main(String ar[]) throws Exception{
		
		Connection con = null;
		Statement stmt =null;
		ResultSet rs =null;
		
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		
		con = DriverManager.getConnection("jdbc:mysql://61.254.11.250:33062/yangpyeong", "yangpyeong", "yangpyeong");
		stmt =con.createStatement();
		rs = stmt.executeQuery("select st_astext(lp.shape) as polygon from land_property as lp join yp_point as yp on lp.a1=yp.PNU");
		
		List<String> polyList = new ArrayList<String>();
		while(rs.next()) {
			polyList.add(rs.getString("polygon"));
		}
		
		int SIZE=polyList.size();
		
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		WKTReader reader = new WKTReader(geometryFactory);
		List<Geometry> list  = new ArrayList<>();
		
		for(int i=0;i<SIZE;i++) {
			String poly =polyList.get(i);
			Polygon polygon = (Polygon)reader.read(poly);
			list.add(polygon);
		}
		
		Object obj = geometryFactory.buildGeometry(list);
		GeometryCollection geometryCollection =(GeometryCollection)obj;
		
		Geometry multiPoly = geometryCollection.union();
		
		TopologyPreservingSimplifier simplePolyg = new TopologyPreservingSimplifier(multiPoly);
		
		System.out.println(simplePolyg.getResultGeometry().toText());
	}

}
