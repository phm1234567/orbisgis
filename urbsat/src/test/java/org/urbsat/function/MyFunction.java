//useless
package org.urbsat.function;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class MyFunction implements Function
{
	
	private Value result = null;
	
	private int constante = 12;
	
	public Function cloneFunction() {
		
		return new MyFunction();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		Object o = (Object) args[0];
		String ts = o.toString();
		int val = Integer.parseInt(ts); 
		return ValueFactory.createValue(val*constante);
	}

	public String getName() {
		
		return "MyFunction";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		
		return false;
	}

}
