/* Generated By:JJTree: Do not edit this line. ASTArgumentList.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.orbisgis.plugins.core.javaManager.parser;

public class ASTArgumentList extends SimpleNode {
	public ASTArgumentList(int id) {
		super(id);
	}

	public ASTArgumentList(JavaParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(JavaParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=bf335dd4fb0c19907483c73cb821c320 (do not edit this
 * line)
 */
