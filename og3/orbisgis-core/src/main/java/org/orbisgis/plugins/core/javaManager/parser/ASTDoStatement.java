/* Generated By:JJTree: Do not edit this line. ASTDoStatement.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.plugins.core.javaManager.parser;

public class ASTDoStatement extends SimpleNode {
	public ASTDoStatement(int id) {
		super(id);
	}

	public ASTDoStatement(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=be733f0609f3ad77977abffe558f8c34 (do not edit this
 * line)
 */
