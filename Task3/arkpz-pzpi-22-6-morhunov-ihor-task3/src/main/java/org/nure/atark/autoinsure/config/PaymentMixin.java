package org.nure.atark.autoinsure.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"clientCredential"})
public abstract class PaymentMixin {}
