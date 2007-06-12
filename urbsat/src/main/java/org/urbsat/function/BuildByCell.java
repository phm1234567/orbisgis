package org.urbsat.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class BuildByCell implements Function {
	private double result = 0;

	public Function cloneFunction() {

		return new BuildByCell();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String dataname = args[args.length - 1].toString();
		String ts = args[3].toString();
		int x = Integer.parseInt(args[0].toString());
		int y = Integer.parseInt(args[1].toString());
		String tog = args[2].toString();
		String type = args[4].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(tog);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Geometry maillon = null;

		try {
			maillon = DataSaved.getMaillon(dataname, x, y);
		} catch (java.lang.IndexOutOfBoundsException e) {

			return ValueFactory
					.createValue("erreur : il n'existe pas de maillon (" + x
							+ "," + y + ")");
		}

		if (ts.equals(type)) {
			if (geom.intersects(maillon)) {
				result++;
			}
		}
		return ValueFactory.createValue(result);
	}

	public String getName() {

		return "BuildByCell";
	}

	public int getType(int[] types) {
		return Type.DOUBLE;
	}

	public boolean isAggregate() {

		return true;
	}

}