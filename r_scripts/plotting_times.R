setwd('~/IdeaProjects/gaceta/')

# Credit: http://www.magesblog.com/2013/04/how-to-change-alpha-value-of-colours-in.html
add.alpha <- function(col, alpha=1){
  if(missing(col))
    stop("Please provide a vector of colours.")
  apply(sapply(col, col2rgb)/255, 2, 
        function(x) 
          rgb(x[1], x[2], x[3], alpha=alpha))  
}

data <- read.csv('nw_split_norm_times.csv', header = FALSE)
colnames(data) <- c('filename', 'bytes', 'processing_time')
# ignore files with zero time
data <- subset(data, processing_time > 0)

plot(data$bytes, data$processing_time,
     col = add.alpha("orange", alpha=0.2),
     pch = 19)

data$log_bytes = log(data$bytes)
data$log_time = log(data$processing_time)

plot(data$log_bytes, data$log_time,
     col = add.alpha("steelblue", alpha=0.2),
     pch = 19,
     xlim = c(0,max(data$log_bytes)))


fit <- lm(log_time ~ log_bytes, data = data)
summary(fit)
coeff0 <- as.numeric(fit$coefficients[1])
coeff1 <- as.numeric(fit$coefficients[2])
abline(a = coeff0, b = coeff1)

# sanity check
summary(exp(coeff0 + coeff1*data$log_bytes))

# total run time for all of split_norm
(ms <- exp(coeff0 + coeff1*log(1.98e+8)))
seconds <- ms/1000
minutes <- seconds/60
hours <- minutes/60
days <- hours/24
days
# 14 days

# average size of file in split_norm is 13
avg_file_size <- mean(data$bytes)
num_files <- 13116
(average_expected_time_ms <- exp(coeff0 + coeff1*log(avg_file_size)))
(total_expected_time_ms <- num_files*average_expected_time_ms)
seconds <- total_expected_time_ms/1000
minutes <- seconds/60
minutes

