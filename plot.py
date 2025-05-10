import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os
import re
from collections import defaultdict

# Define base directory where the CSV files are located
base_dir = '/Users/lushazhang/Projects/SchedulerSimulation/'

# Read all milestone CSV files
all_files = os.listdir(base_dir)
file_names = [
    os.path.join(base_dir, f)
    for f in all_files
    if f.startswith("task_milestones_") and f.endswith(".csv")
]

# Group files by bandwidth (e.g., "task_milestones_0.5_3.0.csv" => 3.0)
bandwidth_pattern = re.compile(r'_(\d+\.?\d*)\.csv$')
bandwidth_to_files = defaultdict(list)

for file in file_names:
    match = bandwidth_pattern.search(file)
    if match:
        bandwidth = float(match.group(1))
        bandwidth_to_files[bandwidth].append(file)

# Sort and keep unique bandwidths for plotting order
bandwidth_values = sorted(bandwidth_to_files.keys())

# For shared curves (computed from first bandwidth group)
x_values = []
avg_original_list = []
avg_Starburst_list = []

# Bandwidth-dependent storage
bandwidth_x = {}
bandwidth_avg_MySim = {}
bandwidth_avg_original_data = {}

# Go through each bandwidth group
for i, bw in enumerate(bandwidth_values):
    x_vals = []
    mysim = []
    original_data = []

    for file in sorted(bandwidth_to_files[bw], key=lambda x: float(x.split('_')[-2])):
        df = pd.read_csv(file)
        df.columns = df.columns.str.strip()
        

        # Compute system load
        total_demand = (df['cpu_required'] * df['original_duration']).sum()
        capacity_period = 960 * df['actual_completion_timestamp'].max()
        system_load = total_demand / capacity_period
        x_vals.append(system_load)

        # Adjust wait time for Starburst-style estimation
        adjusted_actual_wait = np.where(df['transferred'], df['actual_waited_time'] - 1, df['actual_waited_time'])

        # Common curves: only from first bandwidth group
        if i == 0:
            avg_original = df['original_duration'].mean() / 60
            avg_starburst = (df['original_duration'] + adjusted_actual_wait).mean() / 60
            avg_original_list.append(avg_original)
            avg_Starburst_list.append(avg_starburst)
            x_values.append(system_load)
        # print(f" System Load: {system_load:.2f}, Avg Starburst JCT: {avg_starburst:.2f} hours")

        
        # Bandwidth-dependent metrics
        avg_mysim = (df['original_duration'] + adjusted_actual_wait + df['transfer_completion_time']).mean() / 60
        avg_original_data = (df['original_duration'] + df['transfer_completion_time']).mean() / 60
        mysim.append(avg_mysim)
        original_data.append(avg_original_data)

    bandwidth_x[bw] = x_vals
    bandwidth_avg_MySim[bw] = mysim
    bandwidth_avg_original_data[bw] = original_data
    
    last_file = sorted(bandwidth_to_files[bw], key=lambda x: float(x.split('_')[-2]))[-1]
    df_last = pd.read_csv(last_file)
    df_last.columns = df_last.columns.str.strip()

    transferred_tasks = df_last[df_last['transferred'] == True]
    max_completion_time = df_last['actual_completion_timestamp'].max()

    if not transferred_tasks.empty:
        avg_data_transferred = transferred_tasks['data_transferred'].mean()
        num_transferred = len(transferred_tasks)

        ratio = num_transferred / max_completion_time
        # print(last_file)
        print(f"   Bandwidth {bw}: Avg transfer job size = {avg_data_transferred:.2f} MB")
        print(f"   Transferred task count / observation period = {num_transferred} / {max_completion_time} = {ratio:.6f}")
    else:
        print(f"📦 Bandwidth {bw}: No transferred tasks in last file")
        print(f"   • Max actual_completion_timestamp = {max_completion_time}")

    
# Determine Y-axis range
all_y_vals = (
    avg_original_list + avg_Starburst_list +
    sum(bandwidth_avg_MySim.values(), []) +
    sum(bandwidth_avg_original_data.values(), [])
)
min_jct = min(all_y_vals)
max_jct = max(all_y_vals)
y_ticks_jct = np.linspace(min_jct, max_jct, num=6)

# X-axis ticks
x_ticks = np.arange(0.0, 2.5, 0.5)

# Plotting
fig, ax1 = plt.subplots(figsize=(8, 6))

# Common lines
ax1.plot(x_values, avg_original_list, marker='o', markersize=3, color='blue', label='Duration(-dataLoad, -waitTime)')
# Apply a 5% downward proportional offset
adjusted_avg_starburst_list = [v * 0.97 for v in avg_Starburst_list]
ax1.plot(x_values, adjusted_avg_starburst_list, marker='o', markersize=3, color='red',
         label='Starburst (3% offset)')


linestyles = ['-', '--', ':']

# Bandwidth-dependent lines
for idx, bw in enumerate(bandwidth_values):
    linestyle = linestyles[idx % len(linestyles)]
    label_suffix = f"(BW={bw})"
    ax1.plot(bandwidth_x[bw], bandwidth_avg_MySim[bw], marker='o', markersize=3, color='green',
             linestyle=linestyle, label=f'Duration(+dataLoad, +waitTime) {label_suffix}')
    ax1.plot(bandwidth_x[bw], bandwidth_avg_original_data[bw], marker='o', markersize=3, color='orange',
             linestyle=linestyle, label=f'Duration(+dataLoad, -waitTime) {label_suffix}')

# Finalize plot
ax1.set_xlabel("System Load")
ax1.set_ylabel("Average JCT (hours)")
ax1.set_title("Average JCT vs System Load by Bandwidth")
ax1.set_yticks(y_ticks_jct)
ax1.set_xticks(x_ticks)
ax1.legend()
# Annotate peak transfer concurrency in the upper right corner
annotation_text = (
    "Peak transfer concurrency:\n"
    "(bw=10MB/s):  2452 jobs (13258GB)\n"
    "(bw=125MB/s): 16 jobs (13.8GB)"
)


# Add text to upper right with a small padding
ax1.text(1.0, 1.0, annotation_text,
         transform=ax1.transAxes,
         fontsize=10,
         verticalalignment='top',
         horizontalalignment='right',
         bbox=dict(facecolor='white', edgecolor='gray', boxstyle='round,pad=0.3'))


plt.tight_layout()
plt.show()
