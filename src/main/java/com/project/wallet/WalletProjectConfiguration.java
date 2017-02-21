package com.project.wallet;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class WalletProjectConfiguration extends Configuration {
    @NotEmpty
    private String projectName_ = "wallet";
    
    @NotEmpty
    private static int timeOut_ = 5000;
    
    @JsonProperty
    public String getTemplate() {
        return projectName_;
    }

    @JsonProperty
    public void setProjectName(String projectName) {
        this.projectName_ = projectName;
    }
    
    @JsonProperty
    public String getProjectName() {
        return projectName_;
    }
    
    @JsonProperty
    public static int getTimeOut() {
        return timeOut_;
    }
}
