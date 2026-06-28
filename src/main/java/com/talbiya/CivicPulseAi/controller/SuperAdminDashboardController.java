import com.talbiya.CivicPulseAi.dto.MapIssueDTO;
import com.talbiya.CivicPulseAi.dto.SuperAdminDashboardDTO;
import com.talbiya.CivicPulseAi.entity.Issue;
import com.talbiya.CivicPulseAi.service.SuperAdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super-admin/dashboard")
public class SuperAdminDashboardController {

    @Autowired
    private SuperAdminDashboardService dashboardService;

    @GetMapping("/summary")
    public SuperAdminDashboardDTO getSummary() {
        return dashboardService.getDashboardSummary();
    }

    @GetMapping("/map")
    public List<MapIssueDTO> getMapData() {
        return dashboardService.getAllMapIssues();
    }

    @GetMapping("/heatmap")
    public Map<String, Long> getHeatmap() {
        return dashboardService.getAreaHeatmap();
    }

    @GetMapping("/escalated")
    public List<Issue> getEscalated() {
        return dashboardService.getEscalatedIssues();
    }
}