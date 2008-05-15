package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.orbisgis.progress.IProgressMonitor;

public class CreateOperator extends AbstractOperator implements Operator {

	private String tableName;
	private DataSourceFactory dsf;

	public CreateOperator(DataSourceFactory dsf, String tableName) {
		this.tableName = tableName;
		this.dsf = dsf;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		DataSource ds;
		try {
			ds = dsf.getDataSource(getOperator(0).getResult(pm));
			if (!pm.isCancelled()) {
				pm.startTask("Saving result");
				dsf.saveContents(tableName, ds, pm);
				pm.endTask();
			}
			dsf.getSourceManager().remove(ds.getName());
			return null;
		} catch (DriverException e1) {
			throw new ExecutionException("Cannot create table:" + tableName, e1);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

	/**
	 * Validates that the source to create exists in the source manager
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateTableReferences()
	 */
	@Override
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		if (!dsf.exists(tableName)) {
			throw new SemanticException(tableName + " does not exist");
		}

		super.validateTableReferences();
	}

}
