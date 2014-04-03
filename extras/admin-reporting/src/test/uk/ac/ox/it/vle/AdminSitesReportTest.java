package uk.ac.ox.it.vle;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import uk.ac.ox.it.vle.AdminSiteReportWriter;
import uk.ac.ox.it.vle.AdminSitesReport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.sakaiproject.site.api.SiteService.SelectionType;

/**
 * @author Matthew Buckett
 */
@RunWith(MockitoJUnitRunner.class)
public class AdminSitesReportTest {

	public static final String ROLE = "coordinator";
	public static final String DIVISION = "division";

	@Mock
	private UserDirectoryService userDirectoryService;

	@Mock
	private SiteService siteService;

	@Mock
	private AdminSiteReportWriter adminSiteReportWriter;

	@Captor
	ArgumentCaptor<InputStream> captor;

	private AdminSitesReport adminSitesReport;

	@Before
	public void setUp() {
		adminSitesReport = new AdminSitesReport();
		adminSitesReport.setUserDirectoryService(userDirectoryService);
		adminSitesReport.setSiteService(siteService);
		adminSitesReport.setReportWriter(adminSiteReportWriter);
		adminSitesReport.setDivisionNames(Collections.singletonMap(DIVISION, "Division"));
	}

	@Test
	public void testSingleDivision() throws JobExecutionException, IOException {
		Site site = newSite("1", Collections.singleton("user1"));

		User user = newUser("1");

		Mockito.when(siteService.getSites(Matchers.eq(SelectionType.ANY), Matchers.anyString(), Matchers.anyString(), Matchers.anyMap(), Matchers.any(SiteService.SortType.class), Matchers.any(PagingPosition.class))).thenReturn(
				Collections.singletonList(site)
		);
		Mockito.when(userDirectoryService.getUsers(Matchers.any(Collection.class))).thenReturn(Collections.singletonList(user));

		JobExecutionContext ctx = Mockito.mock(JobExecutionContext.class);
		adminSitesReport.execute(ctx);
		Mockito.verify(adminSiteReportWriter, Mockito.atLeastOnce()).writeReport(Matchers.anyString(), Matchers.anyString(), captor.capture(), Matchers.any(AdminSiteReportWriter.Access.class));
		Assert.assertNotNull(captor.getAllValues());
		for (InputStream stream: captor.getAllValues()) {
			IOUtils.copy(stream, System.out);
		}
	}

	private User newUser(String id) {
		User user = Mockito.mock(User.class);
		Mockito.when(user.getDisplayName()).thenReturn("User Display Name "+id);
		Mockito.when(user.getEmail()).thenReturn("user"+id+"@hostname");
		Mockito.when(user.getDisplayId()).thenReturn("user"+id);
		return user;
	}

	private Site newSite(String id, Collection<String> users) {
		Site site = Mockito.mock(Site.class);
		Mockito.when(site.getTitle()).thenReturn("Site Title "+id);
		Mockito.when(site.getId()).thenReturn("siteId"+id);
		Mockito.when(site.getUrl()).thenReturn("http://hostname/portal/siteId"+id);
		Mockito.when(site.getUsersHasRole(ROLE)).thenReturn(Collections.singleton("user"));
		ResourceProperties resourceProperties = Mockito.mock(ResourceProperties.class);
		Mockito.when(site.getProperties()).thenReturn(resourceProperties);
		Mockito.when(resourceProperties.getProperty(AdminSitesReport.DEFAULT_DIVISION_PROP)).thenReturn(DIVISION);
		return site;
	}
}
