import javafx.util.Pair;

public class Face {
	public Vertex[] verts = new Vertex[3];
	public boolean verify(Vertex v) {
		for (int i=0;i<verts.length;i++)
			if (verts[i]!=null&&verts[i]==v)
				return false;
		return true;
	}
	public boolean isInside(double x, double y) {
		Pair<Double,Double> one = new Pair<>(verts[0].x,verts[0].y);
		Pair<Double,Double> two = new Pair<>(verts[1].x,verts[1].y);
		Pair<Double,Double> three = new Pair<>(verts[2].x,verts[2].y);
		Pair<Double,Double> in = new Pair<>(x,y);
		System.out.println(area(one,two,in)+area(two,three,in)+area(three,one,in));
		System.out.println(area(one,two,three));
		System.out.println();
		return equals(area(one,two,in)+area(two,three,in)+area(three,one,in),area(one,two,three));
	}
	private static double area(Pair<Double,Double> a, Pair<Double,Double> b, Pair<Double,Double> c) {
		double AB = sideFromPoints(a,b);
		double BC = sideFromPoints(b,c);
		double CA = sideFromPoints(c,a);
		double perimeter = (AB+BC+CA)/2;
		return Math.sqrt(perimeter*(perimeter-AB)*(perimeter-BC)*(perimeter-CA));
	}
	private static double sideFromPoints(Pair<Double,Double> a,Pair<Double,Double> b){
		return Math.sqrt(Math.pow(a.getKey()-b.getKey(),2) +
				Math.pow(a.getValue()-b.getValue(),2));
	}
	private static boolean equals(double a, double b) {
		return Math.abs(a-b)<1;
	}
}
