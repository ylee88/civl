package dev.civl.abc.ast.node.common.acsl;

import java.io.PrintStream;

import dev.civl.abc.ast.node.IF.IdentifierNode;
import dev.civl.abc.ast.node.IF.SequenceNode;
import dev.civl.abc.ast.node.IF.acsl.PredicateNode;
import dev.civl.abc.ast.node.IF.declaration.VariableDeclarationNode;
import dev.civl.abc.ast.node.IF.expression.ExpressionNode;
import dev.civl.abc.ast.node.IF.statement.CompoundStatementNode;
import dev.civl.abc.ast.node.IF.statement.ReturnNode;
import dev.civl.abc.ast.node.IF.type.FunctionTypeNode;
import dev.civl.abc.ast.node.common.declaration.CommonFunctionDefinitionNode;
import dev.civl.abc.token.IF.Source;

/**
 * A predicate node extends the function definition node. It has 3 children:
 * name, function type node and definition node. The definition node is just a
 * return statement.
 * 
 * @author ziqing
 */
public class CommonPredicateNode extends CommonFunctionDefinitionNode
		implements
			PredicateNode {

	private ExpressionNode bodyExpression = null;

	public CommonPredicateNode(Source source, FunctionTypeNode type,
			IdentifierNode name, CompoundStatementNode definition) {
		super(source, name, (FunctionTypeNode) type, null, definition);
	}

	@Override
	public ContractKind contractKind() {
		return ContractKind.PREDICATE;
	}

	@Override
	public CommonPredicateNode copy() {
		return new CommonPredicateNode(getSource(), duplicate(getTypeNode()),
				duplicate(getPredicateName()),
				duplicate((CompoundStatementNode) child(3)));
	}

	@Override
	public IdentifierNode getPredicateName() {
		return (IdentifierNode) this.child(0);
	}

	@Override
	public SequenceNode<VariableDeclarationNode> getParameters() {
		return ((FunctionTypeNode) this.child(1)).getParameters();
	}

	@Override
	public ExpressionNode getExpressionBody() {
		if (bodyExpression == null) {
			bodyExpression = ((ReturnNode) ((CompoundStatementNode) this
					.child(3)).child(0)).getExpression();
		}
		return bodyExpression;
	}

	@Override
	protected void printBody(PrintStream out) {
		String params = "";
		SequenceNode<VariableDeclarationNode> paramsNode = getParameters();

		for (VariableDeclarationNode binder : paramsNode)
			params += binder.prettyRepresentation() + " ";
		out.print("predicate " + getPredicateName().prettyRepresentation()
				+ " (" + params + ") = " + getBody().prettyRepresentation());
	}

	@Override
	public StringBuffer prettyRepresentation() {
		StringBuffer text = new StringBuffer();

		text.append("predicate " + this.getName() + " ("
				+ getParameters().prettyRepresentation() + ") = "
				+ this.getExpressionBody().prettyRepresentation());
		return text;
	}
}
