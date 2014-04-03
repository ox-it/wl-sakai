package uk.ac.ox.it.vle;

import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.exception.*;

import java.io.InputStream;

/**
 * Simple implementation that writes the content out into content hosting.
 *
 * @author Matthew Buckett
 */
public class AdminSiteReportWriterSakai implements AdminSiteReportWriter {

	private ContentHostingService contentHostingService;
	private String outputFolder;

	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}

	public void setOutputFolder(String outputFolder) {
		// Trim any trailing slash.
		if (outputFolder.endsWith(Entity.SEPARATOR)) {
			outputFolder = outputFolder.substring(0, outputFolder.length()-1);
		}
		this.outputFolder = outputFolder;
	}

	@Override
	public void writeReport(String filename, String mimeType, InputStream stream, Access access) {
		String resourceId = outputFolder + Entity.SEPARATOR + filename;
		try {
			try {
				contentHostingService.removeResource(resourceId);
			} catch (IdUnusedException e) {
				// Expected.
			}
			ContentResourceEdit resource = contentHostingService.addResource(resourceId);
			resource.setContent(stream);
			resource.setContentType(mimeType);
			if (Access.PUBLIC.equals(access)) {
				resource.setPublicAccess();
			}
			contentHostingService.commitResource(resource);
		} catch (InUseException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (TypeException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (PermissionException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (IdUsedException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (IdInvalidException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (InconsistentException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (ServerOverloadException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		} catch (OverQuotaException e) {
			throw new IllegalStateException("Unable to update resource: "+resourceId, e);
		}
	}
}
