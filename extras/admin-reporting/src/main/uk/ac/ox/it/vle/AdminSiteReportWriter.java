package uk.ac.ox.it.vle;

import java.io.InputStream;

/**
 * Interface for writing out the report.
 * @author Matthew Buckett
 */
interface AdminSiteReportWriter {

	enum Access {PRIVATE, PUBLIC};

	public void writeReport(String filename, String mimeType, InputStream stream, Access access);

}
