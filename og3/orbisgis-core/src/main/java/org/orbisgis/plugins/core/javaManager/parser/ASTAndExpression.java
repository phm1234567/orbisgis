/* Generated By:JJTree: Do not edit this line. ASTAndExpression.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.plugins.core.javaManager.parser;

public class ASTAndExpression extends SimpleNode {
	public ASTAndExpression(int id) {
		super(id);
	}

	public ASTAndExpression(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=574f3953b2247d8debbfaf7e9f689a56 (do not edit this
 * line)
 */
